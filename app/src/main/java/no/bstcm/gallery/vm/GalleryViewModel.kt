package no.bstcm.gallery.vm

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import no.bstcm.gallery.unsplash.UnsplashRepository

class GalleryViewModel @ViewModelInject constructor(private val repository: UnsplashRepository) :
    ViewModel() {
}