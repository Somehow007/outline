package com.ujs.outline.controller;

import com.ujs.outline.service.ExportExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 导出为excel相关接口
 */
@RestController
@RequestMapping("/excel/export")
public class ExportExcelController {
    @Autowired
    private ExportExcelService exportExcelService;
    
    @GetMapping("/users")
    public void exportUsersExcel(HttpServletResponse response) throws IOException {
        exportExcelService.exportUserToExcel(response);
    }
}
