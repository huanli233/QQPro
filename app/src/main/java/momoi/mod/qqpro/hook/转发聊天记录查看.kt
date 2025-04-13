package momoi.mod.qqpro.hook

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.tencent.mobileqq.activity.fling.`TopGestureLayout$OnGestureListener`
import com.tencent.qqlive.module.videoreport.inject.fragment.ReportAndroidXDialogFragment
import com.tencent.qqnt.kernel.api.impl.MsgService
import com.tencent.qqnt.kernel.nativeinterface.Contact
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import com.tencent.qqnt.msg.KernelServiceUtil
import com.tencent.watch.aio_impl.data.WatchAIOMsgItem
import com.tencent.watch.aio_impl.ui.cell.base.BaseWatchItemCell
import com.tencent.watch.aio_impl.ui.cell.unsupport.WatchToQQViewMsgItem
import com.tencent.watch.aio_impl.ui.widget.AIOCellGroupWidget
import loadPicElement
import momoi.anno.mixin.Mixin
import momoi.mod.qqpro.lib.FILL
import momoi.mod.qqpro.hook.view.ReplyView
import momoi.mod.qqpro.lib.background
import momoi.mod.qqpro.lib.clickable
import momoi.mod.qqpro.lib.content
import momoi.mod.qqpro.lib.create
import momoi.mod.qqpro.lib.dp
import momoi.mod.qqpro.lib.find
import momoi.mod.qqpro.lib.gravity
import momoi.mod.qqpro.lib.id
import momoi.mod.qqpro.lib.layoutParams
import momoi.mod.qqpro.lib.linearLayout
import momoi.mod.qqpro.lib.margin
import momoi.mod.qqpro.lib.marginVertical
import momoi.mod.qqpro.lib.padding
import momoi.mod.qqpro.lib.paddingHorizontal
import momoi.mod.qqpro.lib.size
import momoi.mod.qqpro.lib.text
import momoi.mod.qqpro.lib.textColor
import momoi.mod.qqpro.lib.textSize
import momoi.mod.qqpro.lib.vertical
import momoi.mod.qqpro.lib.width
import momoi.mod.qqpro.removeAfter
import momoi.mod.qqpro.util.runOnUi
import momoi.mod.qqpro.warp
import java.util.ArrayList

class DetailFragment(private val contact: Contact, private val data: MultiMsgData) : ReportAndroidXDialogFragment(),
    `TopGestureLayout$OnGestureListener` {
    override fun f() {
        dismiss()
    }

    override fun p() {
        dismiss()
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 2115174655)
    }

    private val mMsgList = mutableListOf<MsgRecord>()
    private lateinit var mRv: RecyclerView
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        data.getDetail {
            mMsgList.clear()
            mMsgList.addAll(it)
            runOnUi {
                mRv.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return create<LinearLayout>(inflater.context)
            .vertical()
            .size(FILL, FILL)
            .background(0x77_000000)
            .paddingHorizontal(4.dp)
            .content {
                add<TextView>()
                    .text(data.title)
                    .textSize(13f)
                    .width(FILL)
                    .gravity(Gravity.CENTER)
                    .textColor(0xFF_FFFFFF.toInt())
                    .clickable { dismiss() }
                mRv = add<RecyclerView>()
                    .linearLayout()
                    .layoutParams(LinearLayout.LayoutParams(FILL, 0, 1f))
                    .content(
                        data = mMsgList,
                        factory = {
                            create<LinearLayout>(this)
                                .vertical()
                                .width(FILL)
                                .content {
                                    add<TextView>()
                                        .textSize(12f)
                                        .textColor(0xFF_999999.toInt())
                                        .id(0)
                                    add<LinearLayout>()
                                        .vertical()
                                        .padding(3.dp)
                                        .margin(bottom = 2.dp)
                                        .id(1)
                                }
                        },
                        update = { msg ->
                            find<TextView>(0).text(msg.sendNickName)
                            find<LinearLayout>(1)
                                .apply {
                                    removeAllViews()
                                }
                                .content {
                                    msg.elements.forEach { ele ->
                                        ele.replyElement?.let {
                                            group.background(0xFF_515151.toInt())
                                            add<ReplyView>()
                                                .loadData(contact, it)
                                            return@forEach
                                        }
                                        ele.textElement?.let {
                                            group.background(0xFF_515151.toInt())
                                            add<TextView>()
                                                .textSize(14f)
                                                .textColor(0xFF_FFFFFF.toInt())
                                                .text(it.content)
                                            return@forEach
                                        }
                                        ele.picElement?.let {
                                            add<MyImageView>()
                                                .size(it.picWidth, it.picHeight)
                                                .loadPicElement(it)
                                            return@forEach
                                        }
                                        group.background(0xFF_515151.toInt())
                                        add<TextView>()
                                            .textSize(14f)
                                            .textColor(0xFF_FFFF22.toInt())
                                            .text("不支持的消息类型")
                                    }
                                }
                        })
            }
    }
}

@Mixin
class MultiMsgCellGroup(context: Context) : AIOCellGroupWidget(context) {
    private var multiMsgWidget: View? = null
    fun recovery() {
        multiMsgWidget?.visibility = View.GONE
        contentWidget.visibility = View.VISIBLE
    }

    fun applyMultiMsg(contact: Contact, data: MultiMsgData) {
        if (multiMsgWidget == null) {
            multiMsgWidget = create<LinearLayout>(context)
                .vertical()
                .padding(2.dp)
                .clickable {
                    DetailFragment(contact, data).show(
                        WatchPicElementExtKt.W(this).childFragmentManager,
                        "MultiMsgDetail"
                    )
                }
                .content {
                    add<TextView>()
                        .textSize(13f)
                        .textColor(0xFF_FFFFFF.toInt())
                        .text(data.title)
                    add<TextView>()
                        .textSize(12f)
                        .textColor(0xFF_CCCCCC.toInt())
                        .text(data.previewLines.dropLast(1).joinToString(separator = "\n"))
                    add<View>()
                        .size(width = FILL, height = 1)
                        .background(0xFF_AAAAAA.toInt())
                        .marginVertical(1.dp)
                    add<TextView>()
                        .textSize(10f)
                        .textColor(0xFF_CCCCCC.toInt())
                        .text(data.summary)
                }
            val warp = contentWidget.warp()
            warp.addView(multiMsgWidget, 0)
        }
        contentWidget.visibility = View.GONE
        multiMsgWidget?.visibility = View.VISIBLE
    }
}

@Mixin
class 显示控件 : BaseWatchItemCell() {
    override fun i(
        view: View?,
        item: WatchAIOMsgItem?,
        p3: Int,
        p4: MutableList<*>?,
        p5: Lifecycle?,
        p6: LifecycleOwner?
    ) {
        super.i(view, item, p3, p4, p5, p6)
        (view as? MultiMsgCellGroup)?.let { cell ->
            (item as? MultiForwardMsg)?.multiData?.let {
                cell.applyMultiMsg(this.f().l() ,it)
            } ?: cell.recovery()
        }
    }
}

class MultiMsgData(val contact: Contact, val rawMsg: MsgRecord) {
    val title: String
    val previewLines: List<String>
    val summary: String

    init {
        val content =
            rawMsg.elements?.firstNotNullOf { it.multiForwardMsgElement }
                ?.xmlContent
                ?.replace("&lt;", "<")
                ?.replace("&gt;", ">")
                ?.replace("&amp;", "&")
                ?.replace("&quot;", "\"")
                ?.replace("&apos;", "'")
        val split = content?.split("</title>")?.map {
            it.split(">").last()
        }
        title = split?.getOrNull(0) ?: ""
        previewLines = split?.drop(1) ?: listOf()
        summary = content?.removeAfter("</summary>")?.split(">")?.last() ?: ""
    }

    fun getDetail(callback: (List<MsgRecord>) -> Unit) {
        (KernelServiceUtil.c() as? MsgService)?.service?.getMultiMsg(
            contact, rawMsg.msgId, rawMsg.msgId
        ) { i: Int, s: String, msgRecords: ArrayList<MsgRecord> ->
            callback(msgRecords)
        }
    }
}

@Mixin
class MultiForwardMsg : WatchToQQViewMsgItem() {
    var multiData: MultiMsgData? = null
    override fun o(context: Context?) {
        super.o(context)
        if (this.o == "[聊天记录]") {
            multiData = MultiMsgData(l(), n)
        }
    }
}