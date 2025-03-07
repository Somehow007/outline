package com.ujs.outline.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ujs.outline.common.ResultData;
import com.ujs.outline.common.ResultObj;
import com.ujs.outline.domain.*;
import com.ujs.outline.service.*;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 大纲相关接口
 */
@RestController
@RequestMapping("/outline")
public class OutlineController {
    @Autowired
    CollegeService collegeService;
    @Autowired
    CourseService courseService;
    @Autowired
    MajorService majorService;
    @Autowired
    OutlineService outlineService;
    @Autowired
    OutlineCourseService outlineCourseService;
    @Autowired
    OldOutlineCourseService oldOutlineCourseService;

    @Resource
    CourseController courseController;

    /**
     * 条件、分页查询
     */
    @GetMapping("/")
    public ResultData getAllOutline(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                   @RequestParam(value = "size", defaultValue = "10") Integer size,
                                   @RequestParam(value = "outline_id", defaultValue = "-1") Integer outlineId,
                                   @RequestParam(value = "file_name", defaultValue = "") String fileName,
                                   @RequestParam(value = "en_name", defaultValue = "") String enName,
                                   @RequestParam(value = "college_id", defaultValue = "") String collegeId,
                                   @RequestParam(value = "outline_state", defaultValue = "") String outlineState,
                                    @RequestParam(value = "year", defaultValue = "") String year) {
        QueryWrapper<Outline> wrapper = new QueryWrapper<>();
        if (outlineId != -1) {
            wrapper.eq("outline_id", outlineId);
        } else {
            wrapper.like(!fileName.equals(""), "file_name", fileName);
            wrapper.like(!enName.equals(""), "en_name", enName);
            if (!collegeId.equals("")) {
                List<College> collegeList = collegeService.getCollegeLikeByName(collegeId);
                if (collegeList.size() > 0) {
                    wrapper.and(i -> {
                        i.like("college_id", collegeList.get(0).getCollegeId());
                        for (int j = 1; j < collegeList.size(); j++) {
                            i.or().like("college_id", collegeList.get(j).getCollegeId());
                        }
                    });
                }
            }
            wrapper.like(!outlineState.equals(""), "outline_state", outlineState);
            wrapper.eq(!year.equals(""),"year",year);
        }
        return outlineService.selectWithWrapper(page, size, wrapper);
    }

    /**
     *获取历史大纲列表
     */
    @GetMapping("/searchForOldOutline")
    public ResultData getOldOutlines(@RequestParam(value = "course_id") Integer courseId) {
        List<OldOutlineCourse> oldOutlineCourses = oldOutlineCourseService.selectByCourseId(courseId);
        List<SimpleOutline> slist = new ArrayList<>();
        for (OldOutlineCourse oldOutlineCourse : oldOutlineCourses) {
            Outline outline = outlineService.getById(oldOutlineCourse.getOutlineId());
            if (outline != null) {
                SimpleOutline simpleOutline = new SimpleOutline(outline);
                simpleOutline.setUpdateTime(oldOutlineCourse.getUpdateTime());
                slist.add(simpleOutline);
                System.out.println(simpleOutline.getUpdateTime());
            }
        }
        ResultData resultData = new ResultData();
        resultData.setSize((long) slist.size());
        resultData.setTotal((long) slist.size());
        resultData.setCur(1L);
        resultData.setData(slist);
        return resultData;
    }

    /**
     * 教师上传大纲文档
     */
    @RequestMapping(value = "/user/uploadOutline", method = RequestMethod.POST)
    public ResultObj setCourseToFix(MultipartFile uploadFile,
                                    @RequestParam(value = "enName", defaultValue = "") String enName,
                                    @RequestParam(value = "year", defaultValue = "") String year,
                                    @RequestParam(value = "courseIds", defaultValue = "") String courseIds) throws Exception {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (uploadFile == null) {//上传文件丢失
            return ResultObj.UPLOAD_MISS;
        }
        if (enName.equals("") || year.equals("")||courseIds.equals("")) {//参数为空
            return ResultObj.PARAM_MISS;
        }
        String[] strings = courseIds.split("-");
        if (strings.length == 0) {
            return ResultObj.PARAM_MISS;
        }
        Course tempCourse = courseService.getById(strings[0]);
        if (tempCourse == null) {
            return ResultObj.PARAM_MISS;
        }
        final String CODE=tempCourse.getCode();

        String suffix = uploadFile.getOriginalFilename().substring(
                uploadFile.getOriginalFilename().lastIndexOf("."));
        if (!(suffix.equals(".doc") || suffix.equals(".docx"))) {
            return ResultObj.UPLOAD_ERROR_TYPE;
        }
        //在uploadOutline/学院id 文件夹下保存大纲文件
        String filepath = System.getProperty("user.dir") + File.separator + "uploadOutline";
        filepath += File.separator + user.getCollegeId();
        File dir = new File(filepath);
        if (!dir.exists() && !dir.isDirectory())
            dir.mkdirs();
        String prefix=CODE+"-"+year+"-";
        if (uploadFile.getOriginalFilename().startsWith(prefix))
            prefix="";
        String filename = prefix+
                uploadFile.getOriginalFilename().substring(0, uploadFile.getOriginalFilename().lastIndexOf("."))
                + UUID.randomUUID().toString() + suffix;
        File file = new File(filepath + File.separator + filename);

        uploadFile.transferTo(file);
        //在outline表中保存新建项，重新获取该项id
        Outline outline = new Outline();
        outline.setYear(year);
        outline.setFileName(filename);
        outline.setEnName(enName);
        outline.setUpdateTime(new Date());
        outline.setCollegeId(user.getCollegeId());
        outline.setConnectNum(0);
        outline.setOutlineState("10");
        outlineService.save(outline);

        Map<String, Integer> collegeModify = new HashMap<>();
        Map<String, Integer> majorModify = new HashMap<>();
        Map<Integer, Integer> outlineModify = new HashMap<>();
        outline = outlineService.getByFileNameAndCollegeId(filename,user.getCollegeId());
        //参数带课程id时，同时绑定课程
        int num = 0;
        for (String s : strings) {
            Course course = courseService.getById(s);
            if (course != null && !course.getCourseState().equals("00")) {
                if (course.getCourseState().equals("01")) {
                    String collegeId=course.getCollegeId();
                    String majorId=course.getMajorId();
                    collegeModify.put(collegeId,collegeModify.get(collegeId)==null?-1:collegeModify.get(collegeId)-1);
                    majorModify.put(majorId,collegeModify.get(majorId)==null?-1:collegeModify.get(majorId)-1);
                } else{
                    OutlineCourse outlineCourse = outlineCourseService.getById(s);
                    Integer outlineId = outlineCourse.getOutlineId();
                    outlineModify.put(outlineId,outlineModify.get(outlineId)==null?-1:outlineModify.get(outlineId)-1);
                    //若原来绑定的大纲已经通过了审核，则原绑定信息加入历史大纲绑定表
                    if (course.getCourseState().equals("03")) {
                        OldOutlineCourse oldOutlineCourse = new OldOutlineCourse();
                        oldOutlineCourse.setOutlineId(outlineCourse.getOutlineId());
                        oldOutlineCourse.setCourseId(outlineCourse.getCourseId());
                        oldOutlineCourse.setUpdateTime(new Date());
                        oldOutlineCourseService.save(oldOutlineCourse);
                    }
                }
                course.setCourseState("02");
                courseService.saveOrUpdate(course);
                OutlineCourse outlineCourse = new OutlineCourse();
                outlineCourse.setOutlineId(outline.getOutlineId());
                outlineCourse.setCourseId(course.getCourseId());
                outlineCourseService.saveOrUpdate(outlineCourse);
                num++;
            }
        }
        outline.setConnectNum(num);
        outlineService.saveOrUpdate(outline);
        for (Integer outlineId : outlineModify.keySet()) {
            outlineService.modifyConnectNum(outlineId,outlineModify.get(outlineId));
        }
        for (String key : collegeModify.keySet()) {
            collegeService.modifyRequireNum(key,collegeModify.get(key));
        }
        for (String key : majorModify.keySet()) {
            majorService.modifyRequireNum(key,majorModify.get(key));
        }
        //修改学院上传数量
        collegeService.modifyUploadNum(user.getCollegeId(),1);
        return ResultObj.ADD_SUCCESS;
    }


    /**
     * 审核大纲通过接口
     */
    @PostMapping("/admin/passOutline")
    public ResultObj passOutline(@RequestParam(value = "outline_id") Integer outlineId) {
        if (outlineId == null) {
            return ResultObj.PARAM_MISS;
        }
        Outline outline = outlineService.getById(outlineId);
        if (outline == null) {
            return ResultObj.PARAM_MISS;
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.getCollegeId().equals(outline.getCollegeId())) {
            return ResultObj.UNAUTHORIZED_ERROR;
        }
        outline.setOutlineState("11");
        outline.setCheckTime(new Date());
        outlineService.saveOrUpdate(outline);
        List<OutlineCourse> outlineCourses = outlineCourseService.selectByOutlineId(outlineId);
        for (OutlineCourse outlineCourse : outlineCourses) {
            Course course = courseService.getById(outlineCourse.getCourseId());
            if (course != null) {
                course.setCourseState("03");
                courseService.saveOrUpdate(course);
            }
        }
        return ResultObj.UPDATE_SUCCESS;
    }

    /**
     * 审核大纲未通过接口
     */
    @PostMapping("/admin/failOutline")
    public ResultObj failOutline(@RequestParam(value = "outline_id") Integer outlineId) {
        if (outlineId == null) {
            return ResultObj.PARAM_MISS;
        }
        Outline outline = outlineService.getById(outlineId);
        if (outline == null) {
            return ResultObj.PARAM_MISS;
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.getCollegeId().equals(outline.getCollegeId())) {
            return ResultObj.UNAUTHORIZED_ERROR;
        }

        clear(outlineId);

        return ResultObj.UPDATE_SUCCESS;
    }

    /**
     * 下载大纲
     */
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(@RequestParam(value = "outline_id") String outlineId) {
        Outline outline = outlineService.getById(outlineId);
        System.out.println(outline);
        if (outline == null) {
            return null;
        }
        //未审核大纲下载需要验证权限
        if (outline.getOutlineState().equals("10")) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (user == null || user.getUserState().equals("22")) {
                return null;
            }
        }
        //获取大纲文件
        String filepath = System.getProperty("user.dir") + File.separator + "uploadOutline";
        filepath += File.separator + outline.getCollegeId();
        System.out.println(filepath);
        System.out.println(outline.getFileName());
        File file = new File(filepath + File.separator + outline.getFileName());
        if (!file.exists())
            return null;
        //返回BOS文件流
        String filename = outline.getFileName();
        int index = filename.lastIndexOf(".");
        filename = filename.substring(0, index - 36) + filename.substring(index);
        System.out.println(filename);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment",
                new String(filename.getBytes(), StandardCharsets.ISO_8859_1));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = 0;
            FileInputStream fis = new FileInputStream(file);
            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.flush();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bos.toByteArray(), headers, HttpStatus.CREATED);
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseEntity;
    }

    /**
     * 批量下载大纲
     */
    @RequestMapping(value = "/batchDownload", method = RequestMethod.GET)
    public void batchDownload(@RequestParam(value = "course_id", defaultValue = "-1") Integer courseId,
                                                @RequestParam(value = "code", defaultValue = "") String code,
                                                @RequestParam(value = "name", defaultValue = "") String name,
                                                @RequestParam(value = "course_type", defaultValue = "-1") Integer courseType,
                                                @RequestParam(value = "course_category", defaultValue = "-1") Integer courseCategory,
                                                @RequestParam(value = "major_id", defaultValue = "") String majorId,
                                                @RequestParam(value = "college_id", defaultValue = "") String collegeId,
                                                @RequestParam(value = "term", defaultValue = "-1") Integer term,
                                                @RequestParam(value = "course_state", defaultValue = "") String courseState,
                                                @RequestParam(value = "year", defaultValue = "") String year,
                                                HttpServletResponse response) {
        //查询符合条件课程的所有大纲
        ResultData allCourse = courseController.getAllCourse(0, 99999999,
                courseId, code, name, courseType, courseCategory, majorId, collegeId, term, courseState, year);
        if (Objects.isNull(allCourse) || Objects.isNull(allCourse.getData())) {
            return;
        }
        List<Course> records = (List<Course>) allCourse.getData();
        if (records.isEmpty()) {
            return;
        }
        //条件下大纲id列表
        List<Integer> outlineIds = records.stream().map(Course::getOutlineId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(outlineIds)) {
            return;
        }
        //待下载大纲列表
        List<Outline> outlines = outlineService.listByIds(outlineIds);
        //待下载大纲文件路径
        List<File> fileList = Lists.newArrayList();
        List<String> fileNameList = Lists.newArrayList();
        int num = 1;
        for (Outline outline : outlines) {
            if (outline == null) {
                continue;
            }
            //未审核大纲下载需要验证权限
            if (outline.getOutlineState().equals("10")) {
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (user == null || user.getUserState().equals("22")) {
                    continue;
                }
            }
            //获取大纲文件
            String filepath = System.getProperty("user.dir") + File.separator + "uploadOutline";
            filepath += File.separator + outline.getCollegeId() + File.separator + outline.getFileName();
            File file = new File(filepath);
            if (!file.exists()) {
                continue;
            }
            fileList.add(file);
            String filename = outline.getFileName();
            int index = filename.lastIndexOf(".");
            filename = filename.substring(0, index - 36) + "-" + (num++) + filename.substring(index);
            fileNameList.add(filename);
        }

        downloadZipFiles(response, fileList, fileNameList, "课程大纲.zip");
    }

    /**
     * web下载打成压缩包的文件--流方式
     *
     * @param response 响应
     * @param fileList 文件列表
     * @param zipName  压缩包名
     */
    public static void downloadZipFiles(HttpServletResponse response, List<File> fileList, List<String> fileNameList, String zipName) {
        ZipOutputStream zipOutputStream = null;
        try {
            //设置响应头
            response.reset();
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            //设置文件名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(zipName, "UTF-8"));
            zipOutputStream = new ZipOutputStream(response.getOutputStream());
            for (int i = 0; i < fileList.size(); i++) {
                toZip(zipOutputStream, fileList.get(i), fileNameList.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            //关闭资源
            if (null != zipOutputStream) {
                try {
                    zipOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 压缩文件
     *
     * @param zipOutputStream 压缩文件流
     * @param file            待压缩文件
     */
    private static void toZip(ZipOutputStream zipOutputStream, File file, String filename) {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(Files.newInputStream(file.toPath()));
            //设置压缩包内文件的名称
            zipOutputStream.putNextEntry(new ZipEntry(filename));
            int size;
            byte[] buffer = new byte[4096];
            while ((size = bis.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, size);
            }
            zipOutputStream.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            //关闭资源
            if (null != bis) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 清除大纲的课程绑定
     */
    private void clear(Integer outlineId) {
        Map<String, Integer> collegeModify = new HashMap<>();
        Map<String, Integer> majorModify = new HashMap<>();
        //清除课程绑定
        List<OutlineCourse> outlineCourses = outlineCourseService.selectByOutlineId(outlineId);
        for (OutlineCourse outlineCourse : outlineCourses) {
            Course course = courseService.getById(outlineCourse.getCourseId());
            if (course != null) {
                course.setCourseState("01");
                String collegeId=course.getCollegeId();
                String majorId=course.getMajorId();
                collegeModify.put(collegeId,collegeModify.get(collegeId)==null?1:collegeModify.get(collegeId)+1);
                majorModify.put(majorId,collegeModify.get(majorId)==null?1:collegeModify.get(majorId)+1);
                courseService.saveOrUpdate(course);
            }
            outlineCourseService.removeById(outlineCourse.getCourseId());
        }
        //清除历史大纲表中，该大纲的记录
        oldOutlineCourseService.removeByOutlineId(outlineId);
        //修改需求数
        for (String key : collegeModify.keySet()) {
            collegeService.modifyRequireNum(key,collegeModify.get(key));
        }
        for (String key : majorModify.keySet()) {
            majorService.modifyRequireNum(key,majorModify.get(key));
        }
    }

    /**
     * 删除大纲
     */
    private boolean delete(Integer outlineId) {

        clear(outlineId);

        //删除大纲文件
        Outline outline = outlineService.getById(outlineId);
        String filepath = System.getProperty("user.dir") + File.separator + "uploadOutline";
        filepath += File.separator + outline.getCollegeId();
        File file = new File(filepath + File.separator + outline.getFileName());
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        //修改学院上传数量
        collegeService.modifyUploadNum(outline.getCollegeId(),-1);
        //删除数据库记录
        return outlineService.removeById(outlineId);
    }

    /**
     * 删除单个大纲
     */
    @DeleteMapping("/mainAdmin/delete")
    public ResultObj deleteOutline(@RequestParam(value = "outline_id") Integer outlineId) {
        if (outlineId == null) {
            return ResultObj.PARAM_MISS;
        }
        return delete(outlineId) ? ResultObj.DELETE_SUCCESS : ResultObj.DELETE_ERROR;
    }

    /**
     * 批量删除大纲
     */
    @DeleteMapping("/mainAdmin/deleteAll")
    public ResultObj deleteAllOutline(@RequestParam(value = "outline_ids",defaultValue = "") String outlineIds) {
        int num=0;
        //获取参数
        if (outlineIds.equals("")) {
            return ResultObj.PARAM_MISS;
        }
        String[] strings=outlineIds.split("-");
        for (String s : strings) {
            Integer i = Integer.parseInt(s);
            if(delete(i)) num++;
        }
        return num > 0 ? ResultObj.DELETE_SUCCESS : ResultObj.DELETE_ERROR;
    }

    /**
     * (添加)绑定大纲与课程的一对多关系
     */
    @PostMapping("/user/addAssignOutline")
    public ResultObj addAssignOutlineToCourses(@RequestBody String body) {
        //获取参数
        JSONObject js = JSON.parseObject(body);
        Integer outlineId = js.getInteger("outline_id");
        Object[] courseIds =js.getJSONArray("courseIds").toArray();
        boolean[] state = new boolean[courseIds.length];
        if (outlineId == null || courseIds.length == 0) {
            return ResultObj.PARAM_MISS;
        }
        Outline outline = outlineService.getById(outlineId);
        //不变的课程跳过
        Map<String, Integer> collegeModify = new HashMap<>();
        Map<String, Integer> majorModify = new HashMap<>();
        Map<Integer, Integer> outlineModify = new HashMap<>();
        List<OutlineCourse> oldList = outlineCourseService.selectByOutlineId(outlineId);
        for (OutlineCourse outlineCourse : oldList) {
            for (int i = 0; i < courseIds.length; i++) {
                if (outlineCourse.getCourseId().equals(courseIds[i])) {
                    state[i]=true;
                    break;
                }
            }
        }

        //绑定新添课程
        int sum=0;
        List<Course> newList = new ArrayList<>();
        for (int i = 0; i < courseIds.length; i++) {
            if(state[i]) continue;
            Course course = courseService.getById((Integer)courseIds[i]);
            if (course != null && !course.getCourseState().equals("00")) {
                if (course.getCourseState().equals("01")) {
                    sum++;
                    String collegeId=course.getCollegeId();
                    String majorId=course.getMajorId();
                    collegeModify.put(collegeId,collegeModify.get(collegeId)==null?-1:collegeModify.get(collegeId)-1);
                    majorModify.put(majorId,collegeModify.get(majorId)==null?-1:collegeModify.get(majorId)-1);
                }else{
                    OutlineCourse outlineCourse = outlineCourseService.getById((Integer)courseIds[i]);
                    outlineModify.put(outlineId,outlineModify.get(outlineId)==null?-1:outlineModify.get(outlineId)-1);
                    //若原来绑定的大纲已经通过了审核，则原绑定信息加入历史大纲绑定表
                    if (course.getCourseState().equals("03")) {
                        OldOutlineCourse oldOutlineCourse = new OldOutlineCourse();
                        oldOutlineCourse.setOutlineId(outlineCourse.getOutlineId());
                        oldOutlineCourse.setCourseId(outlineCourse.getCourseId());
                        oldOutlineCourse.setUpdateTime(new Date());
                        oldOutlineCourseService.save(oldOutlineCourse);
                    }
                }
                course.setCourseState(outline.getOutlineState().equals("10")?"02":"03");
                newList.add(course);
                OutlineCourse outlineCourse = new OutlineCourse();
                outlineCourse.setOutlineId(outlineId);
                outlineCourse.setCourseId(course.getCourseId());
                outlineCourseService.saveOrUpdate(outlineCourse);
            }
        }
        if(newList.size()>0)
            courseService.saveOrUpdateBatch(newList);
        if (sum > 0){
            outlineService.modifyConnectNum(outlineId,sum);
        }
        for (Integer key : outlineModify.keySet()) {
            outlineService.modifyConnectNum(key,outlineModify.get(key));
        }
        for (String key : collegeModify.keySet()) {
            collegeService.modifyRequireNum(key,collegeModify.get(key));
        }
        for (String key : majorModify.keySet()) {
            majorService.modifyRequireNum(key,majorModify.get(key));
        }
        return ResultObj.UPDATE_SUCCESS;
    }

    /**
     * (重新)绑定大纲与课程的一对多关系
     */
//    @PostMapping("/user/ReassignOutline")
//    public ResultObj assignOutlineToCourses(@RequestBody String body) {
//        //获取参数
//        JSONObject js = JSON.parseObject(body);
//        Integer outlineId = js.getInteger("outline_id");
//        Object[] courseIds = js.getJSONArray("courseIds").toArray();
//        boolean[] state = new boolean[courseIds.length];
//        if (outlineId == null || courseIds.length == 0) {
//            return ResultObj.PARAM_MISS;
//        }
//        Outline outline = outlineService.getById(outlineId);
//        //清空不需要的课程绑定信息,不变的课程跳过
//        int sum=0;
//        Map<String, Integer> collegeModify = new HashMap<>();
//        Map<String, Integer> majorModify = new HashMap<>();
//        List<OutlineCourse> oldList = outlineCourseService.selectByOutlineId(outlineId);
//        List<Course> updateCourses = new ArrayList<>();
//        for (OutlineCourse outlineCourse : oldList) {
//            boolean flag=true;
//            for (int i = 0; i < courseIds.length; i++) {
//                if (outlineCourse.getCourseId().equals(courseIds[i])) {
//                    state[i]=true;
//                    flag=false;
//                    sum++;
//                    break;
//                }
//            }
//            if (flag) {
//                Course course = courseService.getById(outlineCourse.getOutlineId());
//                if (course != null && !course.getCourseState().equals("00")) {
//                    course.setCourseState("01");
//                    String collegeId=course.getCollegeId();
//                    String majorId=course.getMajorId();
//                    collegeModify.put(collegeId,collegeModify.get(collegeId)==null?1:collegeModify.get(collegeId)+1);
//                    majorModify.put(majorId,collegeModify.get(majorId)==null?1:collegeModify.get(majorId)+1);
//                    updateCourses.add(course);
//                    outlineCourseService.removeById(outlineCourse.getCourseId());
//                }
//            }
//        }
//        if(updateCourses.size()>0)
//            courseService.saveOrUpdateBatch(updateCourses);
//
//        //绑定新添课程
//        List<Course> newList = new ArrayList<>();
//        for (int i = 0; i < courseIds.length; i++) {
//            if(state[i]) continue;
//            Course course = courseService.getById((Integer)courseIds[i]);
//            if (course != null && !course.getCourseState().equals("00")) {
//                if (course.getCourseState().equals("01")) {
//                    sum++;
//                    String collegeId=course.getCollegeId();
//                    String majorId=course.getMajorId();
//                    collegeModify.put(collegeId,collegeModify.get(collegeId)==null?-1:collegeModify.get(collegeId)-1);
//                    majorModify.put(majorId,collegeModify.get(majorId)==null?-1:collegeModify.get(majorId)-1);
//                }else{
//                    OutlineCourse outlineCourse = outlineCourseService.getById((Integer)courseIds[i]);
//                    outlineService.modifyConnectNum(outlineCourse.getOutlineId(),-1);
//                }
//                course.setCourseState(outline.getOutlineState().equals("10")?"02":"03");
//                newList.add(course);
//                OutlineCourse outlineCourse = new OutlineCourse();
//                outlineCourse.setOutlineId(outlineId);
//                outlineCourse.setCourseId(course.getCourseId());
//                outlineCourseService.saveOrUpdate(outlineCourse);
//            }
//        }
//        if(newList.size()>0)
//            courseService.saveOrUpdateBatch(newList);
//        if (sum > 0){
//            outline.setConnectNum(sum);
//            outlineService.save(outline);
//        }
//        for (String key : collegeModify.keySet()) {
//            collegeService.modifyRequireNum(key,collegeModify.get(key));
//        }
//        for (String key : majorModify.keySet()) {
//            majorService.modifyRequireNum(key,majorModify.get(key));
//        }
//        return ResultObj.UPDATE_SUCCESS;
//    }
}
