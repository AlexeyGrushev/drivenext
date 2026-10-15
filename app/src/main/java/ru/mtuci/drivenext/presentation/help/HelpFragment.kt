package ru.mtuci.drivenext.presentation.help

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.FragmentHelpBinding
import ru.mtuci.drivenext.presentation.common.copyToClipboard
import ru.mtuci.drivenext.presentation.common.navigator

/** «Помощь»: частые вопросы с раскрывающимися ответами и обращение в поддержку по почте. */
class HelpFragment : Fragment(R.layout.fragment_help) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentHelpBinding.bind(view)
        binding.backButton.setOnClickListener { navigator.back() }

        val questions = resources.getStringArray(R.array.faq_questions)
        val answers = resources.getStringArray(R.array.faq_answers)
        questions.forEachIndexed { index, question ->
            val item = LayoutInflater.from(requireContext()).inflate(R.layout.item_faq, binding.faqContainer, false)
            val answer = item.findViewById<TextView>(R.id.answer)
            val chevron = item.findViewById<ImageView>(R.id.chevron)
            item.findViewById<TextView>(R.id.question).text = question
            answer.text = answers[index]
            item.setOnClickListener {
                TransitionManager.beginDelayedTransition(binding.faqContainer)
                answer.isVisible = !answer.isVisible
                chevron.rotation = if (answer.isVisible) 180f else 0f
            }
            binding.faqContainer.addView(item)
        }

        val email = getString(R.string.help_support_email)
        binding.writeButton.setOnClickListener { writeToSupport(email) }
        binding.supportEmail.setOnClickListener {
            copyToClipboard(email)
            Snackbar.make(binding.root, R.string.help_email_copied, Snackbar.LENGTH_SHORT).show()
        }
    }

    /** Открывает почтовое приложение с готовым адресом и темой; если почты на устройстве нет — копирует адрес. */
    private fun writeToSupport(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO, "mailto:$email".toUri())
            .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.help_mail_subject))
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            copyToClipboard(email)
            Snackbar.make(requireView(), R.string.help_email_copied, Snackbar.LENGTH_SHORT).show()
        }
    }
}
