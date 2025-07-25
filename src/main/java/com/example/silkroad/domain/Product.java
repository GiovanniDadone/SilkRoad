package com.example.silkroad.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 200)
    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    private String description;

    @NotNull
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Size(max = 255)
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private Category category;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "cart", "product" }, allowSetters = true)
    private Set<CartItem> cartItems = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "order", "product" }, allowSetters = true)
    private Set<OrderItem> orderedItems = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Product id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Product name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Product description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Product price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return this.stockQuantity;
    }

    public Product stockQuantity(Integer stockQuantity) {
        this.setStockQuantity(stockQuantity);
        return this;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public Product imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Product isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Product category(Category category) {
        this.setCategory(category);
        return this;
    }

    public Set<CartItem> getCartItems() {
        return this.cartItems;
    }

    public void setCartItems(Set<CartItem> cartItems) {
        if (this.cartItems != null) {
            this.cartItems.forEach(i -> i.setProduct(null));
        }
        if (cartItems != null) {
            cartItems.forEach(i -> i.setProduct(this));
        }
        this.cartItems = cartItems;
    }

    public Product cartItems(Set<CartItem> cartItems) {
        this.setCartItems(cartItems);
        return this;
    }

    public Product addCartItem(CartItem cartItem) {
        this.cartItems.add(cartItem);
        cartItem.setProduct(this);
        return this;
    }

    public Product removeCartItem(CartItem cartItem) {
        this.cartItems.remove(cartItem);
        cartItem.setProduct(null);
        return this;
    }

    public Set<OrderItem> getOrderedItems() {
        return this.orderedItems;
    }

    public void setOrderedItems(Set<OrderItem> orderItems) {
        if (this.orderedItems != null) {
            this.orderedItems.forEach(i -> i.setProduct(null));
        }
        if (orderItems != null) {
            orderItems.forEach(i -> i.setProduct(this));
        }
        this.orderedItems = orderItems;
    }

    public Product orderedItems(Set<OrderItem> orderItems) {
        this.setOrderedItems(orderItems);
        return this;
    }

    public Product addOrderedItems(OrderItem orderItem) {
        this.orderedItems.add(orderItem);
        orderItem.setProduct(this);
        return this;
    }

    public Product removeOrderedItems(OrderItem orderItem) {
        this.orderedItems.remove(orderItem);
        orderItem.setProduct(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return getId() != null && getId().equals(((Product) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", stockQuantity=" + getStockQuantity() +
            ", imageUrl='" + getImageUrl() + "'" +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
