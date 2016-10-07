package co.edu.udea.compumovil.gr6.permisos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int MY_WRITE_EXTERNAL_STORAGE = 0;
    private String comments = null;
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLayout = findViewById(R.id.linearLayoutMain);

        final EditText editText = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comments = editText.getText().toString();
                verificarPermisos();
            }
        });

    }


    /* Primer Paso
    * Verificar el estado del permiso para la aplicación
    * */
    private void verificarPermisos() {

        int writePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            solicitarPermiso();
        } else {
            guardarComentarios();
        }
    }

    /* Segundo Paso
    * Solicitar el permiso de escritura para la aplicación
    * */
    private void solicitarPermiso() {
        //shouldShowRequestPermissionRationale es verdadero solamente si ya se había mostrado
        //anteriormente el dialogo de permisos y el usuario lo negó
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            mostrarSnackBar();
        } else {
            //si es la primera vez se solicita el permiso directamente
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_WRITE_EXTERNAL_STORAGE);
        }
    }

    /* Tercer Paso
    *  Procesar la respuesta del usuario
    * */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Si el requestCode corresponde al que usamos para solicitar el permiso y
        //la respuesta del usuario fue positiva
        if (requestCode == MY_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                guardarComentarios();
            } else {
                mostrarSnackBar();
            }
        }
    }


    // Metodos de Utilidad


    /**
     * Muestra un snackbar con una acción para abrir "Settings"
     */
    private void mostrarSnackBar() {
        Snackbar.make(mLayout, R.string.permission_write_storage,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        abrirConfiguracion();
                    }
                })
                .show();
    }

    /**
     * Abre el intent de detalles de configuración de nuestra app
     */
    public void abrirConfiguracion() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    /**
     * Guarda el comentario en un archivo dentro de la memoria de nuestro movil
     */
    private void guardarComentarios() {

        if (sePuedeEscribirEnAlmExterno()) {
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "comments.txt");
                boolean created = file.createNewFile();
                if (file.exists()) {
                    OutputStream fo = new FileOutputStream(file, true);
                    fo.write(comments.getBytes());
                    fo.close();
                    Toast.makeText(this, getResources().getString(R.string.message) + " " + file.getPath(), Toast.LENGTH_LONG).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Verifica si se puede escribir o leer el almacenamiento Externo
     */
    public boolean sePuedeEscribirEnAlmExterno() {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
