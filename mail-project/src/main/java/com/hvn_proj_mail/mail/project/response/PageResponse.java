package com.hvn_proj_mail.mail.project.response;

import com.hvn_proj_mail.mail.project.entity.Mail;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public PageResponse(Page<T> result) {
        this.content = result.getContent();
        this.pageNumber = result.getNumber();
        this.pageSize = result.getSize();
        this.totalElements = result.getTotalElements();
        this.totalPages = result.getTotalPages();
        this.last = result.isLast();
    }
}
