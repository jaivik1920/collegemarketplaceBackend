package com.collegemarketplace.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Products {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	@Column(nullable = false)
    private String productname;
    private String productdescription;
	@Column(nullable = false)
    private String productCatagory;
    private String productimageUrl;
	@Column(nullable = false)
    private float productPrice;

    @ManyToOne
    private Users user;
    
    

	public Products() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Products(Long id, String productname, String productdescription, String productCatagory, String productimageUrl, float productPrice) {
		super();
		this.id = id;
		this.productname = productname;
		this.productdescription = productdescription;
		this.productimageUrl = productimageUrl;
		this.productCatagory = productCatagory;
		this.productPrice = productPrice;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductname() {
		return productname;
	}

	public void setProductname(String productname) {
		this.productname = productname;
	}

	public String getProductdescription() {
		return productdescription;
	}

	public void setProductdescription(String productdescription) {
		this.productdescription = productdescription;
	}

	public String getProductimage() {
		return productimageUrl;
	}

	public void setProductimage(String productimageUrl) {
		this.productimageUrl = productimageUrl;
	}

	public String getProductCatagory() {
		return productCatagory;
	}

	public void setProductCatagory(String productCatagory) {
		this.productCatagory = productCatagory;
	}
	
	

	public String getProductimageUrl() {
		return productimageUrl;
	}

	public void setProductimageUrl(String productimageUrl) {
		this.productimageUrl = productimageUrl;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}
	

	public float getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(float productPrice) {
		this.productPrice = productPrice;
	}

	@Override
	public String toString() {
		return "Products [id=" + id + ", productname=" + productname + ", productdescription=" + productdescription
				+ ", productCatagory=" + productCatagory + ", productimageUrl=" + productimageUrl + ", productPrice="
				+ productPrice + "]";
	}
	
	

	
	
	
    
	
    
    
}
