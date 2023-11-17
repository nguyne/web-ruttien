package web.ytbcash.wmoney.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_images")
public class Image {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "image_id", length = 36, nullable = false)
    private String imageId;

    @Column(name = "name_image", length = 6)
    private String nameImage;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Date createdAt;
}
