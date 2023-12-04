package com.example.harolrincon_tr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Clfile {
    private Context context;
    private Activity activity;

    public Clfile(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    private boolean statusPermissionES() {
        int response = ContextCompat.checkSelfPermission(this.context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (response == PackageManager.PERMISSION_GRANTED) return true;
        return false;
    }

    private void requestPermissionES(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this.activity , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},23);
            // Gestion de respuesta
            Toast.makeText(context, "Permiso otorgado" , Toast.LENGTH_SHORT).show();
        }
    }

    private void createDir(File file){
        if(!file.exists()){
            file.mkdirs();
        }
    }

    public void saveFile( String nameFile,String info){
        File directorio = null;
        requestPermissionES();
        if(statusPermissionES()) {
            try {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    directorio = new File(Environment.getExternalStorageDirectory(), "Archivo");
                    createDir(directorio);
                    Toast.makeText(context, "Ruta" + directorio, Toast.LENGTH_SHORT).show();
                } else {
                    directorio = new File(context.getExternalFilesDir(null), "ArchivoAPP");
                    createDir(directorio);
                    Toast.makeText(context, "Ruta" + directorio, Toast.LENGTH_LONG).show();
                }

                if (directorio != null) {
                    File file = new File(directorio, nameFile);
                    FileWriter writer = new FileWriter(file);
                    writer.append(info);
                    writer.flush();
                    writer.close();
                    Toast.makeText(context, "SE HA GUARDADO EL ARCHIVO", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "NO SE PUDO CREAR EL DIRECTORIO", Toast.LENGTH_LONG).show();
                }
            } catch (IOException exception){
                exception.printStackTrace();
                Toast.makeText(context, ""+exception, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context,"No hay permiso", Toast.LENGTH_LONG).show();
        }
    }
}
