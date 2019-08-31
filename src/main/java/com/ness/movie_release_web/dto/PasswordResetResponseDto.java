package com.ness.movie_release_web.dto;

import com.ness.movie_release_web.model.type.MessageDestinationType;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PasswordResetResponseDto {
    private MessageDestinationType source;
    private String address;
}
