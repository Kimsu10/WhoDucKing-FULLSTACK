package hello.dto.user;

import lombok.Data;

@Data
public class HeaderDTO {

    private int point;

    public HeaderDTO(int point) {
        this.point = point;
    }
}