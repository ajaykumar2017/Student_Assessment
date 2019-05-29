package com.tecent.student_assessment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Test_Series_Fragment extends Fragment {
    TextView  algorithm, data_structure, compiler_design, theory_of_computation, database, operating_system,
    digital_logic, computer_organization, computer_network, discrete_math, c_Lang, cpp_Lang, java_lang, python, androids, others;
    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test_series_fragment, container, false);
        algorithm=view.findViewById(R.id.algo);
        data_structure=view.findViewById(R.id.datastr);
        data_structure=view.findViewById(R.id.datastr);
        compiler_design=view.findViewById(R.id.compiler);
        theory_of_computation=view.findViewById(R.id.toc);
        database=view.findViewById(R.id.dbms);
        operating_system=view.findViewById(R.id.operating);
        digital_logic=view.findViewById(R.id.digital);
        computer_organization=view.findViewById(R.id.coa);
        computer_network=view.findViewById(R.id.cn);
        discrete_math=view.findViewById(R.id.dm);
        c_Lang=view.findViewById(R.id.clang);
        cpp_Lang=view.findViewById(R.id.cpplang);
        java_lang=view.findViewById(R.id.javalang);
        python=view.findViewById(R.id.python);
        androids=view.findViewById(R.id.androids);
        others=view.findViewById(R.id.others);

        final Intent intent=new Intent(getActivity(),TestSeriesSubject.class);
        algorithm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("testSubjectShort","algo");
                intent.putExtra("testsubject",algorithm.getText().toString());
                startActivity(intent);
            }
        });
        data_structure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("testSubjectShort","ds");
                intent.putExtra("testsubject",data_structure.getText().toString());
                startActivity(intent);
            }
        });
        compiler_design.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("testSubjectShort","cd");
                intent.putExtra("testsubject",compiler_design.getText().toString());
                startActivity(intent);
            }
        });
        theory_of_computation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("testSubjectShort","toc");
                intent.putExtra("testsubject",theory_of_computation.getText().toString());
                startActivity(intent);
            }
        });
        database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("testSubjectShort","db");
                intent.putExtra("testsubject",database.getText().toString());
                startActivity(intent);
            }
        });
        operating_system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("testSubjectShort","os");
                intent.putExtra("testsubject",operating_system.getText().toString());
                startActivity(intent);
            }
        });
        digital_logic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("testSubjectShort","dl");
                intent.putExtra("testsubject",digital_logic.getText().toString());
                startActivity(intent);
            }
        });
        computer_organization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("testSubjectShort","co");
                intent.putExtra("testsubject",computer_organization.getText().toString());
                startActivity(intent);
            }
        });
        computer_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("testSubjectShort","cn");
                intent.putExtra("testsubject",computer_network.getText().toString());
                startActivity(intent);
            }
        });
        discrete_math.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("testSubjectShort","dm");
                intent.putExtra("testsubject",discrete_math.getText().toString());
                startActivity(intent);
            }
        });
        c_Lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("testSubjectShort","c");
                intent.putExtra("testsubject",c_Lang.getText().toString());
                startActivity(intent);
            }
        });
        cpp_Lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("testSubjectShort","cpp");
                intent.putExtra("testsubject",cpp_Lang.getText().toString());
                startActivity(intent);
            }
        });
        java_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("testSubjectShort","java");
                intent.putExtra("testsubject",java_lang.getText().toString());
                startActivity(intent);
            }
        });
        python.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("testSubjectShort","python");
                intent.putExtra("testsubject",python.getText().toString());
                startActivity(intent);
            }
        });
        androids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("testSubjectShort","android");
                intent.putExtra("testsubject",androids.getText().toString());
                startActivity(intent);
            }
        });
        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("testSubjectShort","other");
                intent.putExtra("testsubject",others.getText().toString());
                startActivity(intent);
            }
        });


        return view;
    }
}
