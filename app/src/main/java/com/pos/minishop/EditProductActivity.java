package com.pos.minishop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.CategoryModel;
import com.pos.minishop.model.ProductCategoryModel;
import com.pos.minishop.model.ProductModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class EditProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ImageView ivProduct;
    private EditText etName, etPrice, etDesc, etStock;
    private Button btnSubmit;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private Uri imageUri;
    private Spinner spinnerProductCategory;
    private ArrayList<ProductCategoryModel> listProductCategory = new ArrayList<>();
    private String idProductCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        ivProduct = findViewById(R.id.iv_imgProduct_input);
        etName = findViewById(R.id.et_name_input);
        etPrice = findViewById(R.id.et_price_input);
        etDesc = findViewById(R.id.et_desc_input);
        etStock = findViewById(R.id.et_stock);
        btnSubmit = findViewById(R.id.btn_inputProduct);
        spinnerProductCategory = findViewById(R.id.sp_input_product);

        requestStoragePermission();
        getProductCategories();

        Intent intent = getIntent();
        ProductModel product = intent.getParcelableExtra("Item Data");

        if (product.getImage() != null) {
            Glide.with(this)
                    .load(product.getImage())
                    .into(ivProduct);
        }

        etName.setText(product.getName());
        etPrice.setText(product.getPrice());
        etDesc.setText(product.getDesc());
        etStock.setText(product.getStock());
        idProductCategory = product.getProductCategoryId();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String price = etPrice.getText().toString().trim();
                String desc = etDesc.getText().toString().trim();
                String stock = etStock.getText().toString().trim();

                boolean isEmpty = false;

                if (name.isEmpty()) {
                    isEmpty = true;
                    etName.setError("Required");
                }

                if (price.isEmpty()) {
                    isEmpty = true;
                    etPrice.setError("Required");
                }

                if (desc.isEmpty()) {
                    isEmpty = true;
                    etDesc.setError("Required");
                }

                if (stock.isEmpty()) {
                    isEmpty = true;
                    etStock.setError("Required");
                }

                if (!isEmpty) {
                    SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                    String token = sp.getString("logged", "missing");

                    File file = null;
                    if (imageUri != null) {
                        file = new File(getPath(imageUri));
                    }

                    AndroidNetworking.put(BaseUrl.url + "api/products/" + product.getId())
                            .addHeaders("Authorization", "Bearer " + token)
                            .addBodyParameter("name", name)
                            .addBodyParameter("price", price)
                            .addBodyParameter("stock", stock)
                            .addBodyParameter("desc", desc)
                            .addBodyParameter("product_category_id", idProductCategory != null ? idProductCategory : "")
                            .setPriority(Priority.HIGH)
                            .build()
                            .setUploadProgressListener(new UploadProgressListener() {
                                @Override
                                public void onProgress(long bytesUploaded, long totalBytes) {

                                }
                            })
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String status = response.getString("status");

                                        if (status.equals("success")) {
                                            Intent intent = new Intent(getApplicationContext(), ProductManagementActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                                    Log.d("TAG", "onError: " + anError.getErrorDetail());
                                    Log.d("TAG", "onError: " + anError.getErrorBody());
                                    Log.d("TAG", "onError: " + anError.getErrorCode());
                                    Log.d("TAG", "onError: " + anError.getResponse());
                                }
                            });
                }
            }
        });
    }

    public void openFileChooser(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Log.d("TAG", "onDebug: " + getPath(imageUri));
            Glide.with(this).load(imageUri).into(ivProduct);
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void getProductCategories() {
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String token = sp.getString("logged", "missing");

        AndroidNetworking.get(BaseUrl.url + "api/product-categories")
                .addHeaders("Authorization", "Bearer " + token)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");

                            if (status.equals("success")) {
                                JSONArray data = response.getJSONArray("data");

                                ProductCategoryModel product = new ProductCategoryModel();
                                product.setNamaCategory("Choose category");
                                listProductCategory.add(product);

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject item = data.getJSONObject(i);

                                    ProductCategoryModel productCategory = new ProductCategoryModel();
                                    productCategory.setId(item.getString("id"));
                                    productCategory.setNamaCategory(item.getString("name"));
                                    listProductCategory.add(productCategory);
                                }

                                showProductCategory();
                                if (idProductCategory != null) {
                                    for(int i = 1; i < listProductCategory.size(); i++) {
                                        String id = listProductCategory.get(i).getId();
                                        if(id.equals(idProductCategory)) {
                                            spinnerProductCategory.setSelection(i);
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("TAG", "onError: " + anError.getErrorDetail());
                        Log.d("TAG", "onError: " + anError.getErrorBody());
                        Log.d("TAG", "onError: " + anError.getErrorCode());
                        Log.d("TAG", "onError: " + anError.getResponse());
                    }
                });
    }

    public void showProductCategory() {
        ArrayAdapter<ProductCategoryModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listProductCategory);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProductCategory.setAdapter(adapter);
        spinnerProductCategory.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ProductCategoryModel productCategory = (ProductCategoryModel) parent.getSelectedItem();
        if (productCategory.equals("Choose category")) {
            idProductCategory = null;
        } else {
            idProductCategory = productCategory.getId();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}