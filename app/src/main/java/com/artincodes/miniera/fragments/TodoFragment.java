package com.artincodes.miniera.fragments;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.artincodes.miniera.R;
import com.artincodes.miniera.utils.todo.ToDoListAdapter;
import com.artincodes.miniera.utils.todo.TodoDBHelper;
import com.melnykov.fab.FloatingActionButton;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodoFragment extends Fragment {

    TodoDBHelper todoDBHelper;
    ListView listViewToDo;
    FloatingActionButton addToDoFab;
    MaterialDialog mMaterialDialog;
    String []tasks;


    public TodoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_todo, container, false);
        listViewToDo = (ListView)rootView.findViewById(R.id.todo_widget_list);
        addToDoFab = (FloatingActionButton)rootView.findViewById(R.id.action_todo_add);

        todoDBHelper = new TodoDBHelper(getActivity());

        updateTodoUI();

        addToDoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View convertView = layoutInflater.inflate(R.layout.add_todo_content, null);

                final EditText todoEditText = (EditText) convertView.findViewById(R.id.dialogEditText);

                mMaterialDialog = new MaterialDialog(getActivity())
                        .setTitle("Add Task")
//                        .setBackground(getActivity().getResources().getDrawable(R.drawable.bg_social_feed_item))
                        .setContentView(convertView)
                        .setNegativeButton("CANCEL", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        })
                        .setPositiveButton("ADD", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String task = todoEditText.getText().toString();
                                todoDBHelper.insertTask(task);
                                updateTodoUI();
                                mMaterialDialog.dismiss();

                            }
                        });
                mMaterialDialog.show();


            }
        });

        listViewToDo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final MaterialDialog dialog = new MaterialDialog(getActivity());
                dialog.setTitle("Delete Task");
                dialog.setMessage("Have you done the task?\n" + tasks[position]);
                dialog.setPositiveButton("Done", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                                TodoDBHelper.TABLE_NAME,
                                todoDBHelper.COLUMN_TASK,
                                tasks[position]);


                        SQLiteDatabase sqlDB = todoDBHelper.getWritableDatabase();
                        sqlDB.execSQL(sql);
                        updateTodoUI();
                        dialog.dismiss();

                    }
                });
                dialog.setNegativeButton("DISCARD", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

                return false;
            }
        });


        return rootView;
    }


    private void updateTodoUI() {

        Cursor c = todoDBHelper.getTask();
        c.moveToFirst();
        int i=0;
        tasks = new String[c.getCount()];
        while (!c.isAfterLast()){
            tasks[i]= c.getString(1);
            c.moveToNext();
            i++;
        }
        ToDoListAdapter toDoListAdapter;
        toDoListAdapter = new ToDoListAdapter(getActivity(), tasks);
        listViewToDo.setAdapter(toDoListAdapter);
    }


}