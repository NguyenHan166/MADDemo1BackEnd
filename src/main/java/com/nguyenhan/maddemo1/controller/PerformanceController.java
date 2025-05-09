package com.nguyenhan.maddemo1.controller;

import com.nguyenhan.maddemo1.constants.ResponseConstants;
import com.nguyenhan.maddemo1.dto.PerformanceDto;
import com.nguyenhan.maddemo1.dto.PersonalWorkDto;
import com.nguyenhan.maddemo1.dto.PersonalWorkUpdateDto;
import com.nguyenhan.maddemo1.mapper.CourseMapper;
import com.nguyenhan.maddemo1.mapper.PersonalWorkMapper;
import com.nguyenhan.maddemo1.model.PersonalWork;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.PersonalWorkRepository;
import com.nguyenhan.maddemo1.service.CourseService;
import com.nguyenhan.maddemo1.service.PerformanceService;
import com.nguyenhan.maddemo1.service.PersonalWorkService;
import com.nguyenhan.maddemo1.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/api/performance")
@RestController
public class PerformanceController {

    private final PerformanceService performanceService;

    public PerformanceController(PersonalWorkMapper personalWorkMapper, PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @PostMapping("/getAllPerformance")
    public ResponseEntity<PerformanceDto> getPerformance(@RequestBody PerformanceDto performanceDto){
        log.info("Performance controller");
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(performanceService.getAllPerformance(performanceDto));
    }
}
