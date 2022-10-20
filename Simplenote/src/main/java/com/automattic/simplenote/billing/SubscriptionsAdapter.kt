package com.automattic.simplenote.billing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.automattic.simplenote.R
import com.automattic.simplenote.viewmodels.IapViewModel

class SubscriptionsAdapter :
    ListAdapter<IapViewModel.PlansListItem, SubscriptionsAdapter.PlanListItemViewHolder>(
        SubscriptionOffersDiffCallback
    ) {

    override fun onBindViewHolder(
        holder: PlanListItemViewHolder,
        position: Int
    ) {
        val uiState = getItem(position)
        holder.onBind(uiState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlanListItemViewHolder = PlanListItemViewHolder(parent)

    class PlanListItemViewHolder(
        internal val parent: ViewGroup,
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.subscription_list_row, parent, false)
    ) {
        private val planName = itemView.findViewById<TextView>(R.id.plan_name)
        private val planPrice = itemView.findViewById<TextView>(R.id.plan_price)
        private val container = itemView.findViewById<View>(R.id.container)


        fun onBind(uiState: IapViewModel.PlansListItem) {
            planName.setText(uiState.plan.period)
            planPrice.text = uiState.plan.price

            container.setOnClickListener {
                uiState.onTapListener?.invoke(uiState.purchaseDetails)
            }

        }
    }

    object SubscriptionOffersDiffCallback :
        DiffUtil.ItemCallback<IapViewModel.PlansListItem>() {
        override fun areItemsTheSame(
            oldItem: IapViewModel.PlansListItem,
            newItem: IapViewModel.PlansListItem
        ): Boolean {
            return oldItem.plan.offerId == newItem.plan.offerId
        }

        override fun areContentsTheSame(
            oldItem: IapViewModel.PlansListItem,
            newItem: IapViewModel.PlansListItem
        ): Boolean {
            return oldItem.plan.period == newItem.plan.period && oldItem.plan.price == newItem.plan.price
        }
    }
}
