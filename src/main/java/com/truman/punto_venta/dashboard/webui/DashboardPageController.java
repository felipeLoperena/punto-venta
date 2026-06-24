package com.truman.punto_venta.dashboard.webui;

import com.truman.punto_venta.dashboard.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardPageController {

    private final DashboardService dashboardService;

    public DashboardPageController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("resumen",      dashboardService.resumen());
        model.addAttribute("umbralStock",  DashboardService.UMBRAL_STOCK_BAJO);
        model.addAttribute("activeModule", "dashboard");
        return "dashboard/index";
    }
}
