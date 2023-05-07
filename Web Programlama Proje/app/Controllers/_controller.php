<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Products_controller extends CI_Controller {
    
    public function index() {
        // Load the model for accessing the products database
        $this->load->model('products_model');
        
        // Get the list of products from the model
        $data['products'] = $this->products_model->get_products();
        
        // Load the view to display the list of products
        $this->load->view('products/list', $data);
    }
    
    public function view($id) {
        // Load the model for accessing the products database
        $this->load->model('products_model');
        
        // Get the product with the specified ID from the model
        $data['product'] = $this->products_model->get_product($id);
        
        // Load the view to display the product details
        $this->load->view('products/details', $data);
    }
    
    public function add() {
        // Load the view to display the form for adding a new product
        $this->load->view('products/add');
    }
    
    public function save() {
        // Load the model for accessing the products database
        $this->load->model('products_model');
        
        // Get the product data from the form
        $product_data = array(
            'name' => $this->input->post('name'),
            'description' => $this->input->post('description'),
            'price' => $this->input->post('price')
        );
        
        // Save the product data to the database
        $this->products_model->save_product($product_data);
        
        // Redirect to the product list page
        redirect('products_controller');
    }
    
    public function edit($id) {
        // Load the model for accessing the products database
        $this->load->model('products_model');
        
        // Get the product with the specified ID from the model
        $data['product'] = $this->products_model->get_product($id);
        
        // Load the view to display the form for editing the product
        $this->load->view('products/edit', $data);
    }
    
    public function update() {
        // Load the model for accessing the products database
        $this->load->model('products_model');
        
        // Get the product data from the form
        $product_data = array(
            'id' => $this->input->post('id'),
            'name' => $this->input->post('name'),
            'description' => $this->input->post('description'),
            'price' => $this->input->post('price')
        );
        
        // Update the product data in the database
        $this->products_model->update_product($product_data);
        
        // Redirect to the product list page
        redirect('products_controller');
    }
    
    public function delete($id) {
        // Load the model for accessing the products database
        $this->load->model('products_model');
        
        // Delete the product with the specified ID from the database
        $this->products_model->delete_product($id);
        
        // Redirect to the product list page
        redirect('products_controller');
    }
}
