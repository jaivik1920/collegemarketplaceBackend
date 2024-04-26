package com.collegemarketplace.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.collegemarketplace.entity.Products;
import com.collegemarketplace.entity.Users;
import com.collegemarketplace.repository.ProductRepository;
import com.collegemarketplace.repository.UserRepository;
//import com.collegemarketplace.services.PasswordService;
import com.collegemarketplace.services.HelperService;
import com.collegemarketplace.services.PasswordService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/collegemarketplace")
@CrossOrigin
@Tag(name = "Rootcontroller API", description = "API for college marketplace")
public class Rootcontroller {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    HelperService helperService;

    @Autowired
    PasswordService passwordService;

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("/addUser")
    @Operation(summary = "Add a user", description = "Adds a new user to the system", tags = {
            "Rootcontroller API" }, responses = {
                    @ApiResponse(description = "User added successfully", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Users.class))),
                    @ApiResponse(description = "Invalid password format", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))) })
    public ResponseEntity<Object> createUser(@RequestBody @Parameter(description = "User to be added", required = true) Users user) {
    	try {
		
        if (!passwordService.isValidPassword(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password format");
        }
        user.setPassword(passwordService.encryptPassword(user.getPassword()));
        Users savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
		} catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong!");
		}
    }

    @PostMapping("/loginUser")
    @Operation(summary = "Login user", description = "Logs in a user with their credentials", tags = {
            "Rootcontroller API" }, responses = {
                    @ApiResponse(description = "User logged in successfully", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Users.class))),
                    @ApiResponse(description = "Invalid credentials", responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))) })
    public ResponseEntity<Object> loginUser(@RequestBody @Parameter(description = "User login details", required = true) Users user) {
    	try {
		System.out.println("hi");
        Users savedUser = userRepository.findByusername(user.getUsername());

        if (savedUser == null || !passwordService.authenticateUser(user.getPassword(), savedUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } else if (passwordService.authenticateUser(user.getPassword(), savedUser.getPassword())) {
            savedUser.setPassword(null);
            return ResponseEntity.ok(savedUser);
        }	
		} catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong!");
		}
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong!");
    }

    @PutMapping("/updateUser")
    @Operation(summary = "Update a user", description = "Updates a user's details in the system", tags = {
            "Rootcontroller API" }, responses = {
                    @ApiResponse(description = "User updated successfully", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Users.class))),
                    @ApiResponse(description = "Invalid password format", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "User not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))) })
    public ResponseEntity<Object> updateUser(@RequestBody @Parameter(description = "User details to be updated", required = true) Users user) {
        try { 
    	if(userRepository.findByusername(user.getUsername()) == null)
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");   	
    	else if (!passwordService.isValidPassword(user.getPassword()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password format");
     
        user.setPassword(passwordService.encryptPassword(user.getPassword()));

            userRepository.save(user);
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong!");
        }
    }

    @PostMapping("addProduct")
    @Operation(summary = "Add a product", description = "Adds a new product to the system", tags = {
            "Rootcontroller API" }, responses = {
                    @ApiResponse(description = "Product added successfully", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Products.class))),
                    @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))) })
    public ResponseEntity<Object> addProducts(@RequestParam("products") @Parameter(description = "Product details to be added", required = true) String products,
            @RequestParam("file") @Parameter(description = "Image file of the product", required = true) MultipartFile file,
            @Parameter(description = "ID of the user who is adding the product", required = true) long userId) throws Exception {
        
    	Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }
        try {
            Products products2 = objectMapper.readValue(products, Products.class);
            // Generate a unique filename
            String filename = helperService.saveFile(file, userId);
            products2.setProductimageUrl(filename);
            products2.setUser(user);
            Products savedProducts = productRepository.save(products2);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProducts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while saving the file: " + e.getMessage());
        }
    }

//    @PostMapping("updateProduct")
//   @Operation(summary = "Update a product", description = "Updates a product's details in the system", tags = {
//            "Rootcontroller API" }, responses = {
//                    @ApiResponse(description = "Product updated successfully", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Products.class))),
//                    @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
//                    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))) })
//    public ResponseEntity<Object> updateProducts(@RequestParam("products") @Parameter(description = "Product details to be updated", required = true) String products,
//            @RequestParam("file") @Parameter(description = "Image file of the product", required = true)MultipartFile file,
//            @Parameter(description = "ID of the user who is updating the product", required = true) long userId) throws Exception {
//        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//        if (file.isEmpty()) {
//            return ResponseEntity.badRequest().body("Please select a file to upload");
//        }
//        try {
//            Products products2 = objectMapper.readValue(products, Products.class);
//            System.out.println(products2.toString());
//            String filename = helperService.saveFile(file, userId);
//            helperService.deleteFile(products2.getProductimageUrl());
//            products2.setProductimageUrl(filename);
//            products2.setUser(user);
//            Products savedProducts = productRepository.save(products2);
//            return ResponseEntity.status(HttpStatus.CREATED).body(savedProducts);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while saving the file: " + e.getMessage());
//        }
//    }

    @GetMapping("/product/{productID}")
    @Operation(summary = "Get product by ID", description = "Get a product by its ID", tags = {
            "Rootcontroller API" }, responses = {
                    @ApiResponse(description = "Product fetched successfully", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Products.class))),
                    @ApiResponse(description = "Product not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))) })
    public ResponseEntity<Object> getProductById(@Parameter(description = "ID of the product", required = true) @PathVariable Long productID) {
    	try {
		
        Optional<Products> products = productRepository.findById(productID);
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product id invalid! Product not found!!");
        } else {
            return ResponseEntity.ok(products.get());
        }
    	
		} catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while saving the file: " + e.getMessage());
		}
    }

    @GetMapping("/category/{productCatagory}")
    @Operation(summary = "Get products by category", description = "Gets a list of products by their category", tags = {
            "Rootcontroller API" }, responses = {
                    @ApiResponse(description = "Products fetched successfully", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Products.class)))),
                    @ApiResponse(description = "Products not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content(mediaType = "application/json",schema = @Schema(implementation = String.class))) })
    public ResponseEntity<Object> getProductsByCategory(@PathVariable @Parameter(description = "Category of the products", required = true) String productCatagory) {
		List<Products> products;
		if(productCatagory.equalsIgnoreCase("all"))
		{
			products = productRepository.findAll();

		}
		else {
			products = productRepository.findByproductCatagory(productCatagory);
		}
		
	    if (products.isEmpty()) {
			 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product id invalid! Product not found!!");
	    } else {
	        return ResponseEntity.ok(products);
	    }
	}
	
//    @GetMapping("/getAllProducts")
//    @Operation(summary = "Get all products", description = "Retrieves a list of all products in the system.", tags = {"Products"})
//    @ApiResponse(responseCode = "200", description = "List of products", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Products.class))))
//    @ApiResponse(responseCode = "404", description = "No products found")
//    public ResponseEntity<List<Products>> getAllProducts() {
//        List<Products> products = productRepository.findAll();
//        if (products.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        } else {
//            return ResponseEntity.ok(products);
//        }
//    }

    @GetMapping("/getAllProductsByUserId/{userID}")
    @Operation(summary = "Get all products by user ID", description = "Retrieves a list of all products associated with a specific user ID.",tags = {
    "Rootcontroller API" })
    @ApiResponse(responseCode = "200", description = "List of products", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Products.class))))
    @ApiResponse(responseCode = "404", description = "No products found for the user", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<Object> getAllProducts(@PathVariable long userID) {
        List<Products> products = productRepository.findByuser_id(userID);
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Products not found!");
        } else {
            return ResponseEntity.ok(products);
        }
    }

    @DeleteMapping("deleteProduct/{productId}/{userId}")
    @Operation(summary = "Delete product by ID", description = "Deletes a product associated with a specific product ID and user ID.",tags = {
    "Rootcontroller API" })
    @ApiResponse(responseCode = "200", description = "Product ID of the deleted product")
    @ApiResponse(responseCode = "401", description = "Unauthorized to delete the product")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<Object> deleteProductById(@PathVariable long productId, @PathVariable long userId) {
    	try {
		
        Optional<Products> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Products not found!");
        }
        if(productOptional.get().getUser().getId() == userId)
        {
            productRepository.deleteById(productId);
            helperService.deleteFile(productOptional.get().getProductimageUrl());
            return ResponseEntity.ok(productId);
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user");
        }
    	
		} catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
    }
	
}



