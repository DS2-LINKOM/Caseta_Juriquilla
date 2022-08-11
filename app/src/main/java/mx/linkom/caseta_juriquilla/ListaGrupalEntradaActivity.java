package mx.linkom.caseta_juriquilla;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mx.linkom.caseta_juriquilla.adaptadores.ListasClassGrid;
import mx.linkom.caseta_juriquilla.adaptadores.adaptador_Modulo;


public class ListaGrupalEntradaActivity extends mx.linkom.caseta_juriquilla.Menu {

    TextView evento;
    private GridView gridList;
    private mx.linkom.caseta_juriquilla.Configuracion Conf;
    JSONArray ja1;
    ArrayList<String> ubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_listagrupalentrada);

        Conf = new mx.linkom.caseta_juriquilla.Configuracion(this);
        ubicacion = new ArrayList<String>();
        evento = (TextView) findViewById(R.id.evento);
        gridList = (GridView) findViewById(R.id.gridList);

        evento.setText(Conf.getEvento());
        invitados();


    }


    public void invitados() {
        String URL = "https://linkaccess2.kap-adm.mx/plataforma/casetaV2/controlador/juriquilla_access/vst_gru_1.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja1 = new JSONArray(response);

                        llenado();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error ", "Id: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> params = new HashMap<>();
                params.put("qr", Conf.getQR().trim());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }




    public void llenado(){
        ArrayList<ListasClassGrid> ubicacion = new ArrayList<ListasClassGrid>();

        for (int i = 0; i < ja1.length(); i += 16) {
            try {

                ubicacion.add(new ListasClassGrid(ja1.getString(i + 7), "ID:"+ja1.getString(i + 0)));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        gridList.setAdapter(new adaptador_Modulo(this, R.layout.activity_listas, ubicacion){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ListasClassGrid) entrada).getTitle());

                    final TextView subtitle = (TextView) view.findViewById(R.id.sub);
                    if (subtitle != null)
                        subtitle.setText(((ListasClassGrid) entrada).getSubtitle());

                    gridList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


                            int posicion=position*16;
                            try {
                                //RONDIN DIA
                               Conf.setIdvisita(ja1.getString(posicion));
                           } catch (JSONException e) {
                                e.printStackTrace();
                           }

                            if(Integer.parseInt(Conf.getPreQr())==1){
                                Conf.setTipoQr("Grupal");
                                Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_juriquilla.EntradasQrActivity.class);
                                startActivity(i);
                                finish();
                            }else{
                                Conf.setTipoReg("Auto");

                                Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_juriquilla.AccesosGrupalActivity.class);
                                startActivity(i);
                                finish();
                            }




                        }
                    });


                }
            }

        });
    }




    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), mx.linkom.caseta_juriquilla.DashboardActivity.class);
        startActivity(intent);
        finish();
    }

}