package com.example.muhammed.pharmacyapp;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddEmployeeActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    EditText nameEditText, birthdayEditText, addressEditText, phoneEditText, userNameEditText, passwordEditText, salaryEditText;
    Spinner typeSpinner, genderSpinner;
    Button addEmployee, cameraPhoto, galleryPhoto;
    View.OnClickListener onClickListener;
    static final int SELECTED_PICTURE_CAMERA = 0, SELECTED_PICTURE_GALLERY = 1, CROP_PICTURE = 2, PLACE_PICKER_REQUEST = 3;
    Uri imageUri;
    String strPictureName, strPicturePath, strName,strBirthday, strAddress, strPhone, strUserName, strPassword, strSalary, strType,
            strGender;

    SharedPreferences shredPreferences;
    SharedPreferences.Editor editor;

    AlertDialog alertDialog;
    File photoFile = null;
    private static final String IMAGE_DIRECTORY_NAME = "VLEMONN";
    String mCurrentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        circleImageView = (CircleImageView)findViewById(R.id.picture);
        nameEditText = (EditText)findViewById(R.id.name);
        birthdayEditText = (EditText)findViewById(R.id.birthday);
        addressEditText = (EditText)findViewById(R.id.address);
        phoneEditText = (EditText)findViewById(R.id.phone);
        userNameEditText = (EditText)findViewById(R.id.userName);
        passwordEditText = (EditText)findViewById(R.id.password);
        salaryEditText = (EditText)findViewById(R.id.salary);
        typeSpinner = (Spinner)findViewById(R.id.type);
        genderSpinner = (Spinner)findViewById(R.id.gender);
        addEmployee = (Button)findViewById(R.id.addEmployee);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        View viewCameraGallery = getLayoutInflater().inflate(R.layout.layout_camera_gallery, null);
        cameraPhoto = (Button) viewCameraGallery.findViewById(R.id.camera);
        galleryPhoto = (Button) viewCameraGallery.findViewById(R.id.gallery);

        AlertDialog.Builder builder = new AlertDialog.Builder(AddEmployeeActivity.this);
        builder.setView(viewCameraGallery);
        alertDialog = builder.create();

        String[] typeEmployee = new String[]{"Type","Employee","Manager"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddEmployeeActivity.this,R.layout.layout_spinner_item,R.id.textViewSpinnerItem,typeEmployee);
        typeSpinner.setAdapter(arrayAdapter);


        String[] genderEmployee = new String[]{"Gender","Male","Female"};
        arrayAdapter = new ArrayAdapter<String>(AddEmployeeActivity.this,R.layout.layout_spinner_item,R.id.textViewSpinnerItem,genderEmployee);
        genderSpinner.setAdapter(arrayAdapter);


        shredPreferences = getSharedPreferences("UserLogin",MODE_PRIVATE);
        editor = shredPreferences.edit();

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.picture:
                        alertDialog.show();
                        break;

                    case R.id.address:
                        PlacePicker.IntentBuilder builder=new PlacePicker.IntentBuilder();
                        Intent intent;
                        try {
                            intent=builder.build(AddEmployeeActivity.this);
                            startActivityForResult(intent,PLACE_PICKER_REQUEST);
                        } catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                        break;

                    case R.id.birthday:
                        DateDialog dialog = new DateDialog(view,false);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        dialog.show(ft, "DatePicker");
                        birthdayEditText.setFocusable(false);
                        birthdayEditText.setClickable(true);
                        break;

                    case R.id.addEmployee:
                        addEmployee();
                        break;

                    case R.id.camera:
                        alertDialog.dismiss();
                        takePhoto();
                        break;


                    case R.id.gallery:
                        alertDialog.dismiss();
                        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(Intent.createChooser(intent,"Select Image From Gallery"),SELECTED_PICTURE_GALLERY);
                        break;

                    default:
                        break;
                }
            }
        };

        circleImageView.setOnClickListener(onClickListener);
        addressEditText.setOnClickListener(onClickListener);
        birthdayEditText.setOnClickListener(onClickListener);
        addEmployee.setOnClickListener(onClickListener);
        cameraPhoto.setOnClickListener(onClickListener);
        galleryPhoto.setOnClickListener(onClickListener);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1){
                    strType = "Employee";
                }
                else if(i==2){
                    strType = "Manager";
                }
                else {
                    strType = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1){
                    strGender = "Male";
                }
                else if(i==2){
                    strGender = "Female";
                }
                else {
                    strGender = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void addEmployee(){
        strName = nameEditText.getText().toString().trim();
        strBirthday = birthdayEditText.getText().toString().trim();
        strAddress = addressEditText.getText().toString().trim();
        strPhone = phoneEditText.getText().toString().trim();
        strUserName = userNameEditText.getText().toString().trim();
        strPassword = passwordEditText.getText().toString().trim();
        strSalary = salaryEditText.getText().toString().trim();


        StringRequest stringRequest =new StringRequest(Request.Method.POST, Config.URL_PHARMACY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response !=null){
                   if(!response.isEmpty()){
                       if(response.equals("Success")){
                           Toast.makeText(AddEmployeeActivity.this,"Employee added successfully",Toast.LENGTH_LONG).show();
                           finish();
                       }
                       else if (response.equals("FailedInsert")){
                           Toast.makeText(AddEmployeeActivity.this,"Failed",Toast.LENGTH_LONG).show();
                       }
                       else if (response.equals("Failed")){
                           Toast.makeText(AddEmployeeActivity.this,"UserName is used previously",Toast.LENGTH_LONG).show();
                       }
                   }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("function","addEmployee");
                map.put("imagePath",strPicturePath);
                map.put("imageName",strPictureName);
                map.put("name",strName);
                map.put("birthday",strBirthday);
                map.put("address",strAddress);
                map.put("phoneNumber",strPhone);
                map.put("userName",strUserName);
                map.put("password",strPassword);
                map.put("type",strType);
                map.put("gender",strGender);
                map.put("salary",strSalary);

                return map;
            }
        };
        Singleton.getSingleton(AddEmployeeActivity.this).setRequestQue(stringRequest);
    }

    public void takePhoto() {
        dispatchTakePictureIntent();
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, SELECTED_PICTURE_CAMERA);
//        }


//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            captureImage();
//        }
//        else
//        {
//            captureImage2();
//        }
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        strPictureName = String.valueOf(System.currentTimeMillis());
//        File photo = new File(Environment.getExternalStorageDirectory(),  strPictureName+"png");
////        intent.putExtra(MediaStore.EXTRA_OUTPUT,
////                Uri.fromFile(photo));
//        //imageUri = Uri.fromFile(photo);
//        startActivityForResult(intent, SELECTED_PICTURE_CAMERA);
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, SELECTED_PICTURE_CAMERA);
    }


    /* Capture Image function for 4.4.4 and lower. Not tested for Android Version 3 and 2 */
    private void captureImage2() {

        try {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            photoFile = createImageFile4();
            if(photoFile!=null)
            {
                Log.i("Mayank",photoFile.getAbsolutePath());
                Uri photoURI  = Uri.fromFile(photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, SELECTED_PICTURE_CAMERA);
            }
        }
        catch (Exception e)
        {
        }
    }

    private void captureImage()
    {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        else
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                try {

                    photoFile = createImageFile();
                    Log.i("Mayank",photoFile.getAbsolutePath());

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "com.vlemonn.blog.captureimage.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        Toast.makeText(this, "44444", Toast.LENGTH_SHORT).show();
                        startActivityForResult(takePictureIntent, SELECTED_PICTURE_CAMERA);
                    }
                } catch (Exception ex) {
                    // Error occurred while creating the File
                }


            }else
            {
            }
        }
    }

    private File createImageFile4()
    {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        strPictureName = String.valueOf(System.currentTimeMillis());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + strPictureName + ".png");

        return mediaFile;

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;

    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, SELECTED_PICTURE_CAMERA);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if (requestCode == SELECTED_PICTURE_GALLERY) {
                if (data != null) {
                    try {
                        imageUri = data.getData();

                        Intent photoPickerCrop = new Intent("com.android.camera.action.CROP");
                        photoPickerCrop.setDataAndType(imageUri, "image/*");
                        photoPickerCrop.putExtra("crop", "true");
                        // indicate aspect of desired crop
                        photoPickerCrop.putExtra("aspectX", 1);
                        photoPickerCrop.putExtra("aspectY", 1);
                        // indicate output X and Y
                        photoPickerCrop.putExtra("outputX", 360);
                        photoPickerCrop.putExtra("outputY", 360);
                        // retrieve data on return
                        photoPickerCrop.putExtra("scaleUpIfNeeded", true);
                        photoPickerCrop.putExtra("return-data", true);

                        startActivityForResult(photoPickerCrop, CROP_PICTURE);
                    } catch (ActivityNotFoundException ex) {

                    }
                }
                else {
                    Toast.makeText(this, "You haven't Selected Picture", Toast.LENGTH_LONG).show();
                }
            }

            else if(requestCode == SELECTED_PICTURE_CAMERA){
                if (data != null) {
//                    Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
//                    circleImageView.setImageBitmap(myBitmap);
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    circleImageView.setImageBitmap(imageBitmap);

                }else if (data==null){
                    galleryAddPic();
                }
            }

            else if(requestCode == CROP_PICTURE){
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    Bitmap selectedImage = bundle.getParcelable("data");
                    circleImageView.setImageBitmap(selectedImage);
                    ByteArrayOutputStream byteArrayOutputStreamObject;
                    byteArrayOutputStreamObject = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
                    byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
                    strPictureName = String.valueOf(System.currentTimeMillis());
                    strPicturePath = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
                    Toast.makeText(this, strPicturePath+"4444444", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "You haven't Crop Picture", Toast.LENGTH_LONG).show();
                }
            }

            else if(requestCode == PLACE_PICKER_REQUEST){
                if (data != null) {
                    Place place = PlacePicker.getPlace(data, this);
                    String addressFromMap = String.valueOf(place.getAddress());
                    addressEditText.setText(addressFromMap);
                }
                else {
                    Toast.makeText(this, "You haven't Selected Address", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

}
