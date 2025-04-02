package com.nguyenhan.maddemo1.mapper;

import com.nguyenhan.maddemo1.dto.ScheduleLearningDto;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.model.ScheduleLearning;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleLearningMapper {

    private final UserRepository userRepository;

    @Autowired
    public ScheduleLearningMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ScheduleLearning mapToScheduleLearning(ScheduleLearningDto scheduleLearningDto, ScheduleLearning scheduleLearning) {
        User user = userRepository.findById(scheduleLearningDto.getUserID()).orElseThrow(
                () -> new ResourceNotFoundException("User", "userId", scheduleLearningDto.getUserID().toString())
        );

        scheduleLearning.setUser(user);
        scheduleLearning.setLearningAddresses(scheduleLearningDto.getLearningAddresses());
        scheduleLearning.setDescription(scheduleLearningDto.getDescription());
        scheduleLearning.setTeacher(scheduleLearningDto.getTeacher());
        scheduleLearning.setState(scheduleLearningDto.getState());
        scheduleLearning.setTimeEnd(scheduleLearningDto.getTimeEnd());
        scheduleLearning.setTimeStart(scheduleLearningDto.getTimeStart());

        return scheduleLearning;
    }

    public ScheduleLearningDto mapToScheduleLearningDto(ScheduleLearning scheduleLearning, ScheduleLearningDto scheduleLearningDto) {
        scheduleLearningDto.setLearningAddresses(scheduleLearning.getLearningAddresses());
        scheduleLearningDto.setDescription(scheduleLearning.getDescription());
        scheduleLearningDto.setTeacher(scheduleLearning.getTeacher());
        scheduleLearningDto.setState(scheduleLearning.getState());
        scheduleLearningDto.setTimeEnd(scheduleLearning.getTimeEnd());
        scheduleLearningDto.setTimeStart(scheduleLearning.getTimeStart());
        scheduleLearningDto.setCourseID(scheduleLearning.getCourse().getId());
        scheduleLearningDto.setUserID(scheduleLearning.getUser().getId());
        return scheduleLearningDto;
    }

}
