package com.nguyenhan.maddemo1.responses;

import com.nguyenhan.maddemo1.dto.NotificationDto;
import lombok.*;

import java.util.List;

@Data
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class NotificationResponse {
    NotificationDto notification;
    List<NotificationDto> notifications;
}
