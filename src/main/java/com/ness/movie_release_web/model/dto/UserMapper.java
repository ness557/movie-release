package com.ness.movie_release_web.model.dto;

import com.ness.movie_release_web.model.User;

import java.util.Optional;

/**
 * UserMapper.
 *
 * @author Roman_Bezruchko
 */
public class UserMapper {

    public static UserDto mapToDto(User user) {
        return new UserDto()
                .setId(user.getId())
                .setRole(user.getRole())
                .setLogin(user.getLogin())
                .setPassword(new PasswordDto().setPassword(user.getEncPassword()))
                .setTelegramId(user.getTelegramId())
                .setTelegramChatId(user.getTelegramChatId())
                .setEmail(user.getEmail())
                .setTelegramNotify(user.isTelegramNotify())
                .setLanguage(user.getLanguage());
    }

    public static User mapToUser(UserDto userDto) {
        return new User()
                .setId(userDto.getId())
                .setRole(userDto.getRole())
                .setLogin(userDto.getLogin())
                .setEncPassword(
                        Optional.ofNullable(userDto.getPassword())
                                .map(PasswordDto::getPassword)
                                .orElse(null))
                .setTelegramId(userDto.getTelegramId())
                .setTelegramChatId(userDto.getTelegramChatId())
                .setEmail(userDto.getEmail())
                .setTelegramNotify(userDto.isTelegramNotify())
                .setLanguage(userDto.getLanguage());
    }
}
