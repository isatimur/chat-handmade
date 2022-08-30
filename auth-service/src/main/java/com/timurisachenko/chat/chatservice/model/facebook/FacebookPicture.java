package com.timurisachenko.chat.chatservice.model.facebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacebookPicture {
    private PictureData data;
}
