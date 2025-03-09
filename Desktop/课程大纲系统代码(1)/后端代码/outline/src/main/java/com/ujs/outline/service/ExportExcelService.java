package com.ujs.outline.service;

import javax.servlet.http.HttpServletResponse;

public interface ExportExcelService {
    void exportUserToExcel(HttpServletResponse response);
}
