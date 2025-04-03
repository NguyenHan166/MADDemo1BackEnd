package com.nguyenhan.maddemo1.controller;

import com.nguyenhan.maddemo1.constants.ResponseConstants;
import com.nguyenhan.maddemo1.dto.ScheduleLearningDto;
import com.nguyenhan.maddemo1.mapper.ScheduleLearningMapper;
import com.nguyenhan.maddemo1.model.ScheduleLearning;
import com.nguyenhan.maddemo1.responses.ResponseDto;
import com.nguyenhan.maddemo1.service.ScheduleLearningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/scheduleLearnings")
public class ScheduleLearningController {

    private final ScheduleLearningService scheduleLearningService;
    public final ScheduleLearningMapper scheduleLearningMapper;

    public ScheduleLearningController(ScheduleLearningService scheduleLearningService, ScheduleLearningMapper scheduleLearningMapper) {
        this.scheduleLearningService = scheduleLearningService;
        this.scheduleLearningMapper = scheduleLearningMapper;
    }

    @GetMapping("/fetch")
    public ResponseEntity<ScheduleLearningDto> fetchScheduleLearningById(Long id) {
        ScheduleLearning scheduleLearning = scheduleLearningService.fetchScheduleLearning(id);
        ScheduleLearningDto dto = scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, new ScheduleLearningDto());
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(dto);
    }

    @GetMapping("/")
    public ResponseEntity<List<ScheduleLearningDto>> fetchAllScheduleLearning() {
        List<ScheduleLearningDto> scheduleLearningDtoList = new ArrayList<>();
        scheduleLearningService.findAll().forEach(
                scheduleLearning -> {
                    scheduleLearningDtoList.add(scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, new ScheduleLearningDto()));
                }
        );
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(scheduleLearningDtoList);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ScheduleLearningDto>> fetchAllScheduleLearningOfUSer(@PathVariable Long userId) {
        List<ScheduleLearningDto> scheduleLearningDtoList = new ArrayList<>();
        scheduleLearningService.findAllByUserID(userId).forEach(
            scheduleLearning -> {
                scheduleLearningDtoList.add(scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, new ScheduleLearningDto()));
            }
        );
        return  ResponseEntity.status(ResponseConstants.STATUS_200).body(scheduleLearningDtoList);
    }

    @PostMapping("/create")
    public ResponseEntity<ScheduleLearning> createScheduleLearning(@RequestBody ScheduleLearningDto dto) {
        ScheduleLearning scheduleLearning = scheduleLearningService.createScheduleLearning(dto);
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(scheduleLearning);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateScheduleLearning(@RequestParam Long id, @RequestBody ScheduleLearningDto dto) {
        boolean isSuccess = scheduleLearningService.updateScheduleLearning(id, dto);
        if (isSuccess) {
            return ResponseEntity.status(ResponseConstants.STATUS_200).body(dto);
        }else {
            return ResponseEntity.status(ResponseConstants.STATUS_417).body(new ResponseDto(ResponseConstants.STATUS_417, ResponseConstants.MESSAGE_417_UPDATE));
        }
    }

}
