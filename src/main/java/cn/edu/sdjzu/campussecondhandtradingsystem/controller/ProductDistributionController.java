package cn.edu.sdjzu.campussecondhandtradingsystem.controller;

import cn.edu.sdjzu.campussecondhandtradingsystem.common.Result;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.ProductDistributionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductDistributionController {

    private final ProductDistributionService productDistributionService;

    public ProductDistributionController(ProductDistributionService productDistributionService) {
        this.productDistributionService = productDistributionService;
    }

    @GetMapping("/distribution")
    public Result<Map<String, Object>> distribution() {
        return Result.success(productDistributionService.getDistribution());
    }
}
