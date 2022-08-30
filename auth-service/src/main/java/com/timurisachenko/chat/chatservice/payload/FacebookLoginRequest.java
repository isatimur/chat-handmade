package com.timurisachenko.chat.chatservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacebookLoginRequest implements Serializable {

    @NotBlank
    private String accessToken;
}
