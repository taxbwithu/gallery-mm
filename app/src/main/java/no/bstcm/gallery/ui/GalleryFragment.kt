package no.bstcm.gallery.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_gallery.*
import no.bstcm.gallery.R
import no.bstcm.gallery.databinding.FragmentGalleryBinding
import no.bstcm.gallery.unsplash.Photo
import no.bstcm.gallery.unsplash.UnsplashPhotoAdapter
import no.bstcm.gallery.unsplash.UnsplashPhotoLoadStateAdapter
import no.bstcm.gallery.vm.GalleryViewModel

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery), UnsplashPhotoAdapter.OnItemClickListener {

    private val viewModel by viewModels<GalleryViewModel>()
    private var loggedIn = false
    private var _binding: FragmentGalleryBinding? = null
    private var menu : Menu? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentGalleryBinding.bind(view)

        val adapter = UnsplashPhotoAdapter(this)

        val glm = GridLayoutManager(view.context, 2)
        glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = adapter.getItemViewType(position)
                return if (viewType == 1) 2
                else 1
            }
        }

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = glm
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = UnsplashPhotoLoadStateAdapter { adapter.retry() },
                footer = UnsplashPhotoLoadStateAdapter { adapter.retry() }
            )
            buttonReload.setOnClickListener {
                adapter.retry()
            }
        }

        viewModel.photos.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progress_bar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                button_reload.isVisible = loadState.source.refresh is LoadState.Error

                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                }
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("result")?.observe(
            viewLifecycleOwner) { result ->
            if(!loggedIn){
                val loginItem = menu?.findItem(R.id.action_login)
                loginItem?.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_logout))
            }
            loggedIn = true
        }

        setHasOptionsMenu(true)
    }

    override fun onItemClick(photo: Photo) {
        if (loggedIn) {
            val action = GalleryFragmentDirections.actionGalleryFragmentToDetailFragment(photo)
            findNavController().navigate(action)
        }
        else {
            openDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_galery, menu)
        this.menu = menu

        if(loggedIn){
            val loginItem = menu.findItem(R.id.action_login)
            loginItem?.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_logout))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_login -> {
                if (loggedIn) {
                    loggedIn = false
                    val loginItem = menu?.findItem(R.id.action_login)
                    loginItem?.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_login))
                }
                else{
                    openDialog()
                }
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun openDialog(){
        findNavController().navigate(R.id.login_dialog)
    }
}