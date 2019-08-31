package com.ness.movie_release_web.dto;

import com.ness.movie_release_web.model.type.NotificationSource;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PasswordResetResponseDto {
    private NotificationSource source;
    private String address;
}
