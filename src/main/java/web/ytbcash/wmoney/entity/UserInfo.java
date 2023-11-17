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
@Table(name = "tbl_user_info")
public class UserInfo {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "info_id", length = 36, nullable = false)
    private String infoId;

    @Column(name = "name_bank")
    private String nameBank;

    @Column(name = "number_bank")
    private String numberBank;

    @Column(name = "name_image_qr_code", length = 6)
    private String nameImageQRCode;

    @Column(name = "image_qr_code", columnDefinition = "TEXT")
    private String imageQRCode;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "status2nd", nullable = false)
    private Boolean status2nd;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Date createdAt;

}
