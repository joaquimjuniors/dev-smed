package br.gov.ba.pmvc.vcapp.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

import br.gov.ba.pmvc.vcapp.R;
import br.gov.ba.pmvc.vcapp.WebViewActivity;

public class MainFragment extends Fragment {

    //class controladora do main_fragment, que por sua vez controla os xml internos Content_main e content_botton_sheet
    View bottomSheet;
    BottomSheetBehavior behavior;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            View view = inflater.inflate(R.layout.main_fragment, container, false);

            setupView(view);
            setRecyclerView(view);

            bottomSheet = view.findViewById(R.id.bottomSheetLayout);
            behavior = BottomSheetBehavior.from(bottomSheet);
            ScreenUtils screenUtils=new ScreenUtils(requireActivity());

            behavior.setPeekHeight(screenUtils.getHeight()-convertDpToPixels(300,requireActivity()));

            return view;

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error :" + e , Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static int convertDpToPixels(float dp, Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float density = metrics.density;
        return (int) Math.ceil(dp * density);
    }


    private void setupView(View view) {
        TextView textView = view.findViewById(R.id.bottom_sheet_text_dice);

        Button btn_home = view.findViewById(R.id.btn_home);

        btn_home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://aprendendosempre.org/")));
                Intent intent = new Intent(requireContext(), WebViewActivity.class);
                intent.putExtra("Link", "https://aprendendosempre.org");
                requireContext().startActivity(intent);
            }
        });

        textView.setText(HtmlCompat.fromHtml(getString(R.string.home_you), HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_sites);

        SiteAdapter adapter = new SiteAdapter(setupList(), requireContext());

        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(requireContext(), 2);
        RecyclerViewMargin decoration = new RecyclerViewMargin(12, 2);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(mLayoutManager);


        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private List<Site> setupList(){
        Bundle bundle = getActivity().getIntent().getExtras();
        String key = "1";
        if(bundle.getString("Chave")!= null)
        {
            key = ""+bundle.getString("Chave");
            Toast toast = Toast.makeText(getContext(),"Line:127 nº: "+bundle.getString("Chave"),Toast.LENGTH_SHORT);
            toast.show();
        }

        List<Site> list = new ArrayList<>();
        list.add(new Site("Artes", R.mipmap.logo_pmvc, "http://smed.pmvc.ba.gov.br/estudoremoto/conteudo.jsp?idSerie="+key+"&idDisciplina=1"));
        list.add(new Site("Língua Portuguesa", R.mipmap.logo_pmvc, "twitter.com"));
        list.add(new Site("Login", R.mipmap.logo_pmvc, "http://smed.pmvc.ba.gov.br/estudoremoto/login-control/"));
        /*Site site = new Site(getString(R.string.aprendendo), R.mipmap.aprendendo_sempre_logo, "https://aprendendosempre.org/");
        Site site1 = new Site(getString(R.string.aprendizap), R.mipmap.aprendizap_logo, "https://www.aprendizap.com.br/");
        Site site2 = new Site(getString(R.string.arvoveredu), R.mipmap.arvore_de_livros, "https://app.arvoreeducacao.com.br/");
        Site site3 = new Site(getString(R.string.avamec____), R.mipmap.avamec, "http://avamec.mec.gov.br/#/");
        Site site4 = new Site(getString(R.string.escoladigi), R.mipmap.escola_digital, "https://escoladigital.org.br/");
        Site site5 = new Site(getString(R.string.googlesala), R.mipmap.google_sala_de_aula, "https://edu.google.com/intl/pt/products/classroom/?modal_active=none");
        Site site6 = new Site(getString(R.string.khanacadem), R.mipmap.khan_academy, "https://pt.khanacademy.org/");
        Site site7 = new Site(getString(R.string.kinedu____), R.mipmap.kinedu, "https://kinedu.com/");
        Site site8 = new Site(getString(R.string.novaescola), R.mipmap.nova_escola, "https://novaescola.org.br/");
        Site site9 = new Site(getString(R.string.smed), R.mipmap.logo_pmvc, "http://smed.pmvc.ba.gov.br/estudoremoto/");


        List<Site> list = new ArrayList<>(10);
        list.add(0, site);
        list.add(1, site1);
        list.add(2, site2);
        list.add(3, site3);
        list.add(4, site4);
        list.add(5, site5);
        list.add(6, site6);
        list.add(7, site7);
        list.add(8, site8);
        list.add(9, site9);*/

        return list;
    }

    public class RecyclerViewMargin extends RecyclerView.ItemDecoration {
        private final int columns;
        private final int margin;

        /**
         * constructor
         * @param margin desirable margin size in px between the views in the recyclerView
         * @param columns number of columns of the RecyclerView
         */
        public RecyclerViewMargin(@IntRange(from=0)int margin , @IntRange(from=0) int columns ) {
            this.margin = margin;
            this.columns=columns;

        }

        /**
         * Set different margins for the items inside the recyclerView: no top margin for the first row
         * and no left margin for the first column.
         */
        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {

            int position = parent.getChildLayoutPosition(view);
            //set right margin to all
            outRect.right = margin;
            //set bottom margin to all
            outRect.bottom = margin;
            //we only add top margin to the first row
            if (position <columns) {
                outRect.top = margin;
            }
            //add left margin only to the first column
            if(position%columns==0){
                outRect.left = margin;
            }
        }
    }


    public class ScreenUtils {

        Context ctx;
        DisplayMetrics metrics;

        public ScreenUtils(Context ctx) {
            this.ctx = ctx;
            WindowManager wm = (WindowManager) ctx
                    .getSystemService(Context.WINDOW_SERVICE);

            Display display = wm.getDefaultDisplay();
            metrics = new DisplayMetrics();
            display.getMetrics(metrics);

        }

        public int getHeight() {
            return metrics.heightPixels;
        }

        public int getWidth() {
            return metrics.widthPixels;
        }

    }
}
