package com.georgcantor.wallpaperapp.ui.fragment

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.ui.adapter.WallpAdapter
import com.georgcantor.wallpaperapp.ui.util.DisposableManager
import com.georgcantor.wallpaperapp.ui.util.EndlessRecyclerViewScrollListener
import com.georgcantor.wallpaperapp.ui.util.HideNavScrollListener
import com.georgcantor.wallpaperapp.ui.util.UtilityMethods
import com.georgcantor.wallpaperapp.ui.util.hideAnimation
import com.georgcantor.wallpaperapp.ui.util.longToast
import com.georgcantor.wallpaperapp.ui.util.shortToast
import com.georgcantor.wallpaperapp.ui.util.showAnimation
import com.georgcantor.wallpaperapp.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.app_bar_main.navigation
import kotlinx.android.synthetic.main.fragment_bmw.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class BmwFragment : Fragment() {

    companion object {
        const val REQUEST = "request"

        fun newInstance(arguments: String): BmwFragment {
            val fragment = BmwFragment()
            val args = Bundle()
            args.putString(REQUEST, arguments)
            fragment.arguments = args

            return fragment
        }
    }

    private lateinit var viewModel: SearchViewModel
    private var adapter: WallpAdapter? = null
    private var columnNo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!UtilityMethods.isNetworkAvailable) {
            requireActivity().longToast(getString(R.string.check_internet))
        }
        viewModel = getViewModel { parametersOf() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_bmw, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!UtilityMethods.isNetworkAvailable) {
            noInternetImageView.visibility = View.VISIBLE
        }

        bmwRefreshLayout.setOnRefreshListener {
            loadData(1)
            bmwRefreshLayout.isRefreshing = false
        }
        checkScreenSize()

        val gridLayoutManager =
            StaggeredGridLayoutManager(columnNo, StaggeredGridLayoutManager.VERTICAL)
        bmwRecyclerView.setHasFixedSize(true)
        bmwRecyclerView.layoutManager = gridLayoutManager

        val scrollListener = object : EndlessRecyclerViewScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadData(page)
            }
        }
        scrollListener.resetState()
        bmwRecyclerView.addOnScrollListener(scrollListener)
        adapter = WallpAdapter(requireContext())
        bmwRecyclerView.adapter = adapter

        val hideScrollListener = object : HideNavScrollListener(requireActivity().navigation) {}
        bmwRecyclerView.addOnScrollListener(hideScrollListener)

        loadData(1)
    }

    @SuppressLint("CheckResult")
    private fun loadData(index: Int) {
        animationView?.showAnimation()

        val disposable =
            viewModel.getPictures(arguments?.getString(REQUEST) ?: "", index).subscribe({
                adapter?.setPicList(it.hits)
                animationView?.hideAnimation()
            }, {
                animationView?.hideAnimation()
                requireActivity().shortToast(getString(R.string.something_went_wrong))
            })

        DisposableManager.add(disposable)
    }

    private fun checkScreenSize() {
        val screenSize =
            resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

        columnNo = when (screenSize) {
            Configuration.SCREENLAYOUT_SIZE_XLARGE -> 4
            Configuration.SCREENLAYOUT_SIZE_UNDEFINED -> 3
            Configuration.SCREENLAYOUT_SIZE_LARGE -> 3
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> 2
            Configuration.SCREENLAYOUT_SIZE_SMALL -> 2
            else -> 2
        }
    }

    override fun onDestroy() {
        DisposableManager.dispose()
        super.onDestroy()
    }

}
