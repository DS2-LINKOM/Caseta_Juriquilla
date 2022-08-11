package mx.linkom.caseta_juriquilla;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mx.linkom.caseta_juriquilla.adaptadores.ModuloClassGrid;
import mx.linkom.caseta_juriquilla.adaptadores.adaptador_Modulo;

public class DashboardActivity extends  mx.linkom.caseta_juriquilla.Menu {
    private FirebaseAuth fAuth;
    private Configuracion Conf;
    JSONArray ja1,ja2,ja3;
    TextView perma,sali,entr,nombre;
    TextView permaT,saliT,entrT;
    String var1,var2,var3,var4,var5;
    String var6,var7,var8,var9,var10;
    LinearLayout rlVistantes,rlTrabajadores;

    private GridView gridList,gridList2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        fAuth = FirebaseAuth.getInstance();
        Conf = new Configuracion(this);
        gridList = (GridView)findViewById(R.id.gridList);
        gridList2 = (GridView)findViewById(R.id.gridList2);
        nombre = (TextView)findViewById(R.id.nombre);

        perma = (TextView)findViewById(R.id.setPermanecen);
        sali = (TextView)findViewById(R.id.setSalidas);
        entr = (TextView)findViewById(R.id.setEntradas);

        permaT = (TextView)findViewById(R.id.setPermanecenT);
        saliT = (TextView)findViewById(R.id.setSalidasT);
        entrT = (TextView)findViewById(R.id.setEntradasT);

        rlVistantes = (LinearLayout)findViewById(R.id.rlVistantes);
        rlTrabajadores = (LinearLayout)findViewById(R.id.rlTrabajadores);
        nombre.setText(Conf.getNomResi());

    }
    @Override
    public void onStart() {
        super.onStart();

        Registro();
        Sesion();
        menu();
    }



    public void Registro (){
        try {
            fAuth.createUserWithEmailAndPassword(Global.EMAIL, Global.PASS)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.i("TAG","Se ha registrado exitosamente");
                            } else {
                                Log.e("TAG","Ha fallado el registro " + task.getException());

                            }
                        }
                    });
        } catch (Exception e) {
            Log.e("TAG","Error ",e);
        }
    }

    public void Sesion (){
        try {
            fAuth.signInWithEmailAndPassword(Global.EMAIL, Global.PASS)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.i("TAG","Se ha logeado exitosamente");
                            } else {
                                Log.e("TAG","Ha fallado la autenticación " + task.getException());
                            }
                        }
                    });
        }catch (Exception e) {
            Log.e("TAG","Error ",e);
        }
    }


    public void menu() {

        String URL = "https://linkaccess2.kap-adm.mx/plataforma/casetaV2/controlador/juriquilla_access/menu.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if(response.equals("error")){
                    Info2();
                }else {
                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja3 = new JSONArray(response);

                            Contador();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Usuario y/o Contraseña Incorrectos", Toast.LENGTH_LONG).show();

                            e.printStackTrace();
                        }
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }



    public void Contador(){

        String URL = "https://linkaccess2.kap-adm.mx/plataforma/casetaV2/controlador/juriquilla_access/contadores.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja1 = new JSONArray(response);
                        Contador2();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG","Error: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void Contador2(){

        String URL = "https://linkaccess2.kap-adm.mx/plataforma/casetaV2/controlador/juriquilla_access/contadoresT.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja2 = new JSONArray(response);


                        Info();
                        llenado();
                        llenado2();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG","Error: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }



    public void Info2() {
        rlVistantes.setVisibility(View.GONE);
        rlTrabajadores.setVisibility(View.GONE);

    }

        public void Info(){
        try {
            //RESIDENTES

            if (ja3.getString(2).equals("1") || ja3.getString(3).equals("1") || ja3.getString(4).equals("1")) {
                rlVistantes.setVisibility(View.VISIBLE);
                if (ja1.getString(0) != "null") {
                    var1 = ja1.getString(0);

                } else {
                    var1 = "0";
                }

                if (ja1.getString(1) != "null") {
                    var2 = ja1.getString(1);
                } else {
                    var2 = "0";
                }

                if (ja1.getString(2) != "null") {
                    var3 = ja1.getString(2);
                } else {
                    var3 = "0";
                }

                if (ja1.getString(3) != "null") {
                    var4 = ja1.getString(3);
                } else {
                    var4 = "0";
                }

                if (ja1.getString(4) != "null") {
                    var5 = ja1.getString(4);
                } else {
                    var5 = "0";
                }


                int ent = Integer.parseInt(var2) + Integer.parseInt(var3);
                int per = Integer.parseInt(var4) + Integer.parseInt(var5);

                sali.setText(var1);
                entr.setText(String.valueOf(ent));
                perma.setText(String.valueOf(per));

            }else{
                rlVistantes.setVisibility(View.GONE);
            }

            //TRABAJADORES
            if(ja3.getString(5).equals("1")){
                rlTrabajadores.setVisibility(View.VISIBLE);
            if (ja2.getString(0) != "null") {
                var6 = ja2.getString(0);

            } else {
                var6 = "0";
            }

            if (ja2.getString(1) != "null") {
                var7 = ja2.getString(1);
            } else {
                var7 = "0";
            }

            if (ja2.getString(2) != "null") {
                var8 = ja2.getString(2);
            } else {
                var9 = "0";
            }

            if (ja2.getString(3) != "null") {
                var9 = ja2.getString(3);
            } else {
                var9 = "0";
            }

            if (ja2.getString(4) != "null") {
                var10 = ja2.getString(4);
            } else {
                var10 = "0";
            }


            int ent2 = Integer.parseInt(var7) + Integer.parseInt(var8);
            int per2 = Integer.parseInt(var9) + Integer.parseInt(var10);

            saliT.setText(var6);
            entrT.setText(String.valueOf(ent2));
            permaT.setText(String.valueOf(per2));
        }else{
                rlTrabajadores.setVisibility(View.GONE);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void llenado(){
        ArrayList<ModuloClassGrid> lista = new ArrayList<ModuloClassGrid>();

        try {
            if(ja3.getString(2).equals("1") || ja3.getString(3).equals("1") || ja3.getString(5).equals("1")  ){
                lista.add(new ModuloClassGrid(R.drawable.ic_baseline_house_24,"Entradas y Salidas","#FF4081"));
            }else{

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }


        gridList.setAdapter(new adaptador_Modulo(this, R.layout.activity_modulo_lista, lista){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    ImageView add = (ImageView) view.findViewById(R.id.imageView);
                    if (add != null)
                        add.setImageResource(((ModuloClassGrid) entrada).getImagen());

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ModuloClassGrid) entrada).getTitle());
                    final LinearLayout line = (LinearLayout) view.findViewById(R.id.line);
                    if (line != null)
                        line.setBackgroundColor(Color.parseColor(((ModuloClassGrid) entrada).getColorCode()));

                    gridList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                    if (position == 0 ){
                                        Intent docugen = new Intent(getApplication(), mx.linkom.caseta_juriquilla.EntradasSalidasActivity.class);
                                    startActivity(docugen);
                                    finish();
                                        }



                                }
                            });
                        }
                }
        });
    }

    public void llenado2(){
        ArrayList<ModuloClassGrid> lista2 = new ArrayList<ModuloClassGrid>();

        try {


            if(ja3.getString(6).equals("1") || ja3.getString(7).equals("1") || ja3.getString(8).equals("1") || ja3.getString(9).equals("1") || ja3.getString(10).equals("1") ){
                lista2.add(new ModuloClassGrid(R.drawable.reportes,"Registros","#4cd2c7"));
            }else{
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        gridList2.setAdapter(new adaptador_Modulo(this, R.layout.activity_modulo_lista, lista2){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    ImageView add = (ImageView) view.findViewById(R.id.imageView);
                    if (add != null)
                        add.setImageResource(((ModuloClassGrid) entrada).getImagen());

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ModuloClassGrid) entrada).getTitle());
                    final LinearLayout line = (LinearLayout) view.findViewById(R.id.line);
                    if (line != null)
                        line.setBackgroundColor(Color.parseColor(((ModuloClassGrid) entrada).getColorCode()));

                    gridList2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                                Intent docugen = new Intent(getApplication(), ReportesActivity.class);
                                startActivity(docugen);
                                finish();



                        }
                    });
                }
            }
        });
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(intent);
        finish();
    }


}