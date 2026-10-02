package com.example.madlab.experiment9

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.madlab.experiment9.databinding.FragmentWebViewBinding

class WebViewFragment : Fragment() {

    private var _binding: FragmentWebViewBinding? = null
    private val binding get() = _binding!!

    private var currentUrl: String = DEFAULT_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUrl = it.getString(ARG_URL, DEFAULT_URL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWebView()
        setupControls()

        loadUrl(currentUrl)
    }

    private fun setupWebView() {
        val settings: WebSettings = binding.webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                url?.let {
                    binding.etUrl.setText(it)
                    currentUrl = it
                }
            }
        }

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress < 100) {
                    binding.webViewProgressBar.visibility = View.VISIBLE
                    binding.webViewProgressBar.progress = newProgress
                } else {
                    binding.webViewProgressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun setupControls() {
        binding.btnBack.setOnClickListener {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                Toast.makeText(context, "No back history", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnForward.setOnClickListener {
            if (binding.webView.canGoForward()) {
                binding.webView.goForward()
            } else {
                Toast.makeText(context, "No forward history", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRefresh.setOnClickListener {
            refreshPage()
        }

        binding.btnGo.setOnClickListener {
            var url = binding.etUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://$url"
                }
                loadUrl(url)
            }
        }
    }

    fun loadUrl(url: String) {
        currentUrl = url
        binding.etUrl.setText(url)
        binding.webView.loadUrl(url)
    }

    fun refreshPage() {
        binding.webView.reload()
        Toast.makeText(context, "Page Refreshed", Toast.LENGTH_SHORT).show()
    }

    fun openExternalBrowser() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_URL = "arg_url"
        private const val DEFAULT_URL = "https://developer.android.com"

        fun newInstance(url: String): WebViewFragment {
            val fragment = WebViewFragment()
            val args = Bundle().apply {
                putString(ARG_URL, url)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
