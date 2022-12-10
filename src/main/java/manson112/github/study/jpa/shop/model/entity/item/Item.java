package manson112.github.study.jpa.shop.model.entity.item;

import lombok.Getter;
import lombok.Setter;
import manson112.github.study.jpa.shop.model.entity.BaseEntity;
import manson112.github.study.jpa.shop.model.entity.Category;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
public class Item extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    List<Category> category = new ArrayList<Category>();
}
