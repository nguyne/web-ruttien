package web.ytbcash.wmoney.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_banks")
public class Banks {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", length = 11, nullable = false)
    private int id;

    @Column(name = "name_bank", length = 50, nullable = false)
    private String nameBank;

    @Column(name = "logo_bank", nullable = false)
    private String logo;
}
