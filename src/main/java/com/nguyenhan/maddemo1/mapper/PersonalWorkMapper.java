package com.nguyenhan.maddemo1.mapper;


import com.nguyenhan.maddemo1.dto.PersonalWorkDto;
import com.nguyenhan.maddemo1.dto.PersonalWorkUpdateDto;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.PersonalWork;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.CourseRepository;
import com.nguyenhan.maddemo1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component // Biến CourseMapper thành Spring Bean
public class PersonalWorkMapper {

    private final UserRepository userRepository; // Không cần static
    private final CourseRepository courseRepository;

    @Autowired // Tiêm UserRepository qua constructor
    public PersonalWorkMapper(UserRepository userRepository,
                              CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public PersonalWork toPersonalWork(PersonalWorkDto personalWorkDto) {
        PersonalWork personalWork = new PersonalWork();

        User user = userRepository.findById(personalWorkDto.getUserId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "userId", personalWorkDto.getUserId().toString())
        );

//        Course course = courseRepository.findById(personalWorkDto.getCourseId()).orElseThrow(
//                ()-> new ResourceNotFoundException("Course", "courseId", personalWorkDto.getCourseId().toString())
//        );

//        if(!(personalWorkDto.getCourseId()==null)) {
//            Course course = courseRepository.findById(personalWorkDto.getCourseId()).orElseThrow(
//                    () -> new ResourceNotFoundException("Course", "courseId", personalWorkDto.getCourseId().toString())
//            );
//            personalWork.setCourse(course);
//        }

        personalWork.setName(personalWorkDto.getName());
        personalWork.setDescription(personalWorkDto.getDescription());
        personalWork.setTimeEnd(personalWorkDto.getTimeEnd());
        personalWork.setTimeStart(personalWorkDto.getTimeStart());
        personalWork.setCreate_at(personalWorkDto.getCreate_at());
        personalWork.setWorkAddress(personalWorkDto.getWorkAddress());
        personalWork.setLoopValue(personalWorkDto.getLoopValue());
        personalWork.setUser(user);


        return personalWork;
    }

    public PersonalWorkDto toPersonalWorkDto(PersonalWork personalWork) {
        PersonalWorkDto personalWorkDto =new PersonalWorkDto();
        personalWorkDto.setId(personalWork.getId());
        personalWorkDto.setName(personalWork.getName());
        personalWorkDto.setDescription(personalWork.getDescription());
        personalWorkDto.setTimeEnd(personalWork.getTimeEnd());
        personalWorkDto.setTimeStart(personalWork.getTimeStart());
        personalWorkDto.setCreate_at(personalWork.getCreate_at());
        personalWorkDto.setCreate_at(personalWork.getCreate_at());
        personalWorkDto.setWorkAddress(personalWork.getWorkAddress());
        personalWorkDto.setUserId(personalWork.getUser().getId());

        return personalWorkDto;
    }

    public void updatePersonalWorkMapper(PersonalWork personalWork, PersonalWorkUpdateDto personalWorkUpdateDto){
//        if(!(personalWorkUpdateDto.getCourseId()==null)) {
//            Course course = courseRepository.findById(personalWorkUpdateDto.getCourseId()).orElseThrow(
//                    () -> new ResourceNotFoundException("Course", "courseId", personalWorkUpdateDto.getCourseId().toString())
//            );
//            personalWork.setCourse(course);
//        }
        personalWork.setName(personalWorkUpdateDto.getName());
        personalWork.setDescription(personalWorkUpdateDto.getDescription());
        personalWork.setTimeEnd(personalWorkUpdateDto.getTimeEnd());
        personalWork.setTimeStart(personalWorkUpdateDto.getTimeStart());
        personalWork.setWorkAddress(personalWorkUpdateDto.getWorkAddress());
        personalWork.setLoopValue(personalWorkUpdateDto.getLoopValue());
    }
}