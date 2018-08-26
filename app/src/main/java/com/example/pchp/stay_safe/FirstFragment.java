package com.example.pchp.stay_safe;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;


/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment
{
String namee="";
String number="";
String relation="";
    String n,m,r,h;
String cid="";
ListView lv1;
    int selectedPosition = -1;
    ContactAdapter adapter;
    Button bt_add_contact;
    Dialog d;




    public FirstFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);


    }

    class frag implements Runnable {
        Socket sock;
        PrintWriter pw;
        BufferedReader br;


        @Override
        public void run() {
            if (GlobalClass.alContacts.size() == 0) {
                try {
                    SharedPreferences pref = getActivity().getSharedPreferences("pref1", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    String h = pref.getString("email", "null");
                    editor.commit();


                    sock = new Socket(GlobalClass.ipAddressServer, 8084);
//                sock = new Socket("192.168.43.66", 8084);
//                sock = new Socket("192.168.2.106", 8084);
                    pw = new PrintWriter(sock.getOutputStream());
                    br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                    String query = "/WithYou/mviewcontact?email=" + h;
                    query = query.replace(" ", "%20");
                    pw.println("GET " + query + " HTTP/1.1");
                    pw.println("host: " +GlobalClass.ipAddressServer+ ":8084");
//                    pw.println("host: 192.168.43.66 :8084");
//                pw.println("host: 192.168.2.106:8084");
                    pw.println("connection: close");
                    pw.println("");
                    pw.flush();
                    while (true) {
                        String p = br.readLine();
                        if (p == null || p.isEmpty()) {
                            break;
                        }

                    }
                    GlobalClass.db.execSQL("delete from contacts");

                    while (true) {
                        String s = br.readLine();
                        if (s == null || s.isEmpty())
                            break;
                        StringTokenizer st = new StringTokenizer(s, "^");

                        namee = st.nextToken();
                        number = st.nextToken();
                        cid = st.nextToken();
                        relation = st.nextToken();
                        GlobalClass.alContacts.add(new MyContacts(cid,namee,number,relation));

                             String qquery = "INSERT INTO contacts VALUES(" + Integer.parseInt(cid) + ",'" + namee + "'," + Long.parseLong(number) + ")";
                             GlobalClass.db.execSQL(qquery);

                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void onResume() {
        super.onResume();
        new Thread(new frag()).start();
        lv1 = (ListView) getActivity().findViewById(R.id.lv11);
       adapter = new ContactAdapter();
        lv1.setAdapter(adapter);
        registerForContextMenu(lv1);
        bt_add_contact = (Button) getActivity().findViewById(R.id.bt_add_contacts);
        bt_add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d = new Dialog(getActivity());
                d.setContentView(R.layout.add_contact_dialog_design);
                d.setTitle("Add New Contact");
                d.setCancelable(false);

                final EditText etContactName = (EditText) d.findViewById(R.id.etContactName);
                final EditText etContactNo = (EditText) d.findViewById(R.id.etContactNumber);
                final Spinner spRelation = (Spinner) d.findViewById(R.id.spRelation);
                ArrayList<String> al1 = new ArrayList<>();
                al1.add("Friends");
                al1.add("Family");
                al1.add("Colleagues");
                al1.add("Work");
                al1.add("Others");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, al1);
                spRelation.setAdapter(adapter);
                ImageButton btAdd = (ImageButton) d.findViewById(R.id.btAdd);
                ImageButton btPickContacts = (ImageButton) d.findViewById(R.id.btPickContacts);
                ImageButton btClose = (ImageButton) d.findViewById(R.id.btClose);
                btAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (etContactName.getText().toString().isEmpty() || etContactNo.getText().toString().isEmpty() || etContactNo.length()<10) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "Invalid Contact", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            new Thread(new AddNewContactJob(etContactName.getText().toString()+"", etContactNo.getText().toString(), spRelation.getSelectedItem().toString())).start();
                        }
                    }
                });
                btPickContacts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent it = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

                        startActivityForResult(it, 70);

                    }
                });
                btClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                d.show();

            }
        });

    }


    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Edit");
//        menu.add(0, v.getId(), 0, "Action 3");
    }
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo menuInfo;//you can select on context item selected
        menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
       int  itemIndex = menuInfo.position;//you will get position of selected item
        selectedPosition=itemIndex;
        final int cid = Integer.parseInt(GlobalClass.alContacts.get(itemIndex).id);

        if (item.getTitle() == "Delete") {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Delete Contact ?");
            builder.setMessage("This Contact will be deleted.");
            builder.setIcon(R.drawable.icon_delete);
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Thread(new DeleteContactJob(cid)).start();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog ad = builder.create();
            ad.show();

        } else if (item.getTitle() == "Edit")
        {
               final Dialog d = new Dialog(getContext());
                d.setContentView(R.layout.design_dialog);

                final TextView tv1 = (TextView) d.findViewById(R.id.tv1);
                final TextView tv2 = (TextView) d.findViewById(R.id.tv2);
                final Spinner rel = (Spinner) d.findViewById(R.id.rel);
                Button btDismiss = (Button) d.findViewById(R.id.btDismiss);
                Button btRandom = (Button) d.findViewById(R.id.btRandom);
                ArrayList<String> all = new ArrayList<>();
            all.add("other");
                all.add("Friends");
                all.add("Family");
                all.add("Colleague");
            all.add("Brother");

                ArrayAdapter<String> adapterr =
                        new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, all);

            rel.setAdapter(adapterr);
                tv1.setText(GlobalClass.alContacts.get(itemIndex).contactName);
            tv2.setText(GlobalClass.alContacts.get(itemIndex).contactNo);
//               rel.setSelection(al.get(itemIndex).relationn);
                btDismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                btRandom.setOnClickListener(new View.OnClickListener() {
                                                @Override

                  public void onClick(View v) {
                   new Thread(new edit(tv1.getText().toString(),tv2.getText().toString(),(String)rel.getSelectedItem(),cid+"")).start();
                   d.dismiss();
                    }
                    }
                );

            d.setCancelable(false);
                    d.setTitle("Edit Data");
                    d.show();

                }
            return true;
    }

    class DeleteContactJob implements Runnable
    {
        int id;
        public DeleteContactJob( int id)
        {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                Socket sock;
                PrintWriter pw;
                BufferedReader br;

                sock = new Socket(GlobalClass.ipAddressServer,8084);

                pw = new PrintWriter(sock.getOutputStream());
                br = new BufferedReader(new InputStreamReader(sock.getInputStream()));


                String qq = "/WithYou/mobileDeleteContactServlet?id=" + id;
                qq = qq.replace(" ", "%20");

                pw.println("GET " + qq + " HTTP/1.1");
                pw.println("host: " +GlobalClass.ipAddressServer+ ":8084");
                pw.println("connection: close");
                pw.println();
                pw.flush();

                while (true) {
                    String s = br.readLine();
                    if (s == null || s.isEmpty()) {
                        break;
                    }
                }
                final String s = br.readLine();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),s, Toast.LENGTH_SHORT).show();
                        GlobalClass.alContacts.remove(selectedPosition);
                        adapter.notifyDataSetChanged();
                    }
                });
                GlobalClass.db.execSQL("delete from contacts where id=" + id);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class edit implements Runnable{
        Socket sock;
        PrintWriter pw;
        BufferedReader br;
        String o,po,r,id;
        edit(String o,String po,String r,String id)
        {

            this.o=o;
            this.po=po;
            this.r=r;
            this.id=id;


        }

        @Override
        public void run() {

                try {
                    SharedPreferences pref = getActivity().getSharedPreferences("pref1", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    String h = pref.getString("email", "null");
                    editor.commit();

                    sock = new Socket(GlobalClass.ipAddressServer, 8084);
//                sock = new Socket("192.168.43.66", 8084);
//                sock = new Socket("192.168.2.106", 8084);
                    pw = new PrintWriter(sock.getOutputStream());
                    br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                    String query = "/WithYou/meditcontact?email=" + h + "&name=" + o + "&number=" + po + "&relation=" + r + "&cid=" + id;
                    query = query.replace(" ", "%20");
                    pw.println("GET " + query + " HTTP/1.1");
                    pw.println("host: " +GlobalClass.ipAddressServer+ ":8084");
//                    pw.println("host: 192.168.43.97:8084");
//                pw.println("host: 192.168.2.106:8084");
                    pw.println("connection: close");
                    pw.println("");
                    pw.flush();
                    while (true) {
                        String p = br.readLine();
                        if (p == null || p.isEmpty()) {
                            break;
                        }

                    }
                    final String s = br.readLine();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                            GlobalClass.alContacts.clear();
                            new Thread(new frag()).start();
                        }
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }

    }
    class ContactAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return GlobalClass.alContacts.size();
        }

        @Override
        public Object getItem(int position)
        {
            return GlobalClass.alContacts.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position,
                            View singleView, ViewGroup parent)
        {
            LayoutInflater inflater = LayoutInflater.from
                    (parent.getContext());
            singleView = inflater.inflate
                    (R.layout.view_contacts,
                            parent,false);

            TextView tvName = (TextView)singleView.findViewById(R.id.tvName);
            TextView tvNumber = (TextView)singleView.findViewById(R.id.tvnumber);
            TextView tvRelation = (TextView)singleView.findViewById(R.id.tvRel);

            tvName.setText(GlobalClass.alContacts.get(position).contactName);
            tvNumber.setText(GlobalClass.alContacts.get(position).contactNo);
            tvRelation.setText(GlobalClass.alContacts.get(position).relation);

            return singleView;
        }
    }
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == 70 && resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
            if (c.moveToFirst()) {
                int id = c.getInt(c.getColumnIndex(ContactsContract.Contacts._ID));
                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                EditText et1 =(EditText) d.findViewById(R.id.etContactName);
                et1.setText(name+"");


                int hasPhoneNum = c.getInt(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if (hasPhoneNum == 1) // --- Contact Has a Phone Number
                {
                    Uri uri2 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    Cursor cnew = getActivity().getContentResolver().query(uri2, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);

                    if (cnew.moveToNext()) {
                        String phoneNumber = cnew
                                .getString(cnew
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        EditText et2 = (EditText) d.findViewById(R.id.etContactNumber);
                        String p=phoneNumber.toString().trim();
                        String q=p.replaceAll("\\s","");
                        if(q.length()>10)
                            q=q.substring(3,13);
                        et2.setText(q);
                    }
                } else {
                    // No number saved for this contact
                }

                // TODO Fetch other Contact details as you want to use

            }
        }
    }


    class AddNewContactJob implements Runnable
    {
        String contactName;
        String contactNo;
        String relation;
        public AddNewContactJob(String contactName, String contactNo, String relation)
        {
            this.contactName = contactName;
            this.contactNo = contactNo;
            this.relation = relation;
        }
        @Override
        public void run() {
            try {
                Socket sock;
                PrintWriter pw;
                BufferedReader br;

                sock = new Socket(GlobalClass.ipAddressServer,8084);

                pw = new PrintWriter(sock.getOutputStream());
                br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                SharedPreferences pref = getActivity().getSharedPreferences("pref1", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                String h = pref.getString("email", "null");
                editor.commit();

                String qq = "/WithYou/maddcontact?email=" + h + "&name=" + contactName + "&phoneno=" + contactNo + "&relation=" + relation;
                qq = qq.replace(" ", "%20");

                pw.println("GET " + qq + " HTTP/1.1");
                pw.println("host: " +GlobalClass.ipAddressServer+ ":8084");
                pw.println("connection: close");
                pw.println();
                pw.flush();

                while (true) {
                    String s = br.readLine();
                    if (s == null || s.isEmpty()) {
                        break;
                    }
                }

                final String s = br.readLine();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                        d.dismiss();
                        GlobalClass.alContacts.clear();
                        new Thread(new frag()).start();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
