package com.example.forum.controller;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.service.CommentService;
import com.example.forum.service.ReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ForumController {
    @Autowired
    ReportService reportService;
    @Autowired
    CommentService commentService;
    @Autowired
    HttpSession session;

    /*
     * 投稿内容表示処理
     */
    @GetMapping
    public ModelAndView top(@RequestParam(name = "start", required = false)LocalDate start, @RequestParam(name = "end", required = false)LocalDate end) {
        ModelAndView mav = new ModelAndView();
        // 投稿を日時で絞り込み取得
        List<ReportForm> contentData = reportService.findByCreatedDateRange(start, end);
        // コメントを全件取得
        List<CommentForm> commentData = commentService.findAllComment();

        Object error = session.getAttribute("error");
        if(error != null) {
            mav.addObject("error", error);
            session.removeAttribute("error");
        }
        // 画面遷移先を指定
        mav.setViewName("/top");
        // 投稿データオブジェクトを保管
        mav.addObject("contents", contentData);
        mav.addObject("commentsData", commentData);
        mav.addObject("comments", new CommentForm());
        mav.addObject("start", start);
        mav.addObject("end", end);

        return mav;
    }

    /*
     * 新規投稿画面表示
     */
    @GetMapping("/new")
    public ModelAndView newContent() {
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        ReportForm reportForm = new ReportForm();
        // 画面遷移先を指定
        mav.setViewName("/new");
        // 準備した空のFormを保管
        mav.addObject("formModel", reportForm);
        return mav;
    }

    /*
     * 新規投稿処理
     */
    @PostMapping("/add")
    public ModelAndView addContent(@ModelAttribute("formModel") @Validated ReportForm reportForm, BindingResult result) {
        if (result.hasErrors()) {
            // バリデーションエラー時、new画面へ戻す
            return new ModelAndView("/new");
        }
        // 投稿をテーブルに格納
        reportService.saveReport(reportForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿削除処理
     */
    @DeleteMapping("/delete/{id}")
    public ModelAndView deleteContent(@PathVariable Integer id) {
        //投稿をテーブルに格納
        reportService.deleteReport(id);
        //rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿編集画面への遷移
     */
    @GetMapping("/edit/{id}")
    public ModelAndView editContent(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        //編集対象の投稿を取得
        ReportForm report = reportService.editReport(id);
        //編集する投稿をセット
        mav.addObject("formModel", report);
        //画面遷移先を指定
        mav.setViewName("/edit");
        return mav;
    }

    /*
     * 投稿編集処理
     */
    @PutMapping("/update/{id}")
    public ModelAndView updateContent(@PathVariable Integer id, @ModelAttribute("formModel") @Validated ReportForm report, BindingResult result) {
        if (result.hasErrors()) {
            // バリデーションエラー時、edit画面へ戻す
            return new ModelAndView("/edit");
        }
        // UrlParameterのidを更新するentityにセット
        report.setId(id);
        // 編集した投稿を更新
        reportService.saveReport(report);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }


    /*
     * コメント投稿処理
     */
    @PostMapping("/comment/{contentId}")
    public ModelAndView addComment(@ModelAttribute("commentForm") @Validated CommentForm comment, BindingResult result, @PathVariable Integer contentId) {
        if (result.hasErrors()) {
            ObjectError error = result.getAllErrors().get(0);
            session.setAttribute("error",error);
            // バリデーションエラー時、top画面へ戻す
            return new ModelAndView("redirect:/");
        }
        //コメントがどの投稿に紐づくか設定
        comment.setReportId(contentId);
        //コメントをテーブルへ格納
        commentService.saveComment(comment);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント編集画面への遷移
     */
    @GetMapping("/editComment/{id}")
    public ModelAndView editComment(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        //編集対象の投稿を取得
        CommentForm comment = commentService.editComment(id);
        //編集する投稿をセット
        mav.addObject("commentForm", comment);
        //画面遷移先を指定
        mav.setViewName("/editComment");
        return mav;
    }

    /*
     * コメント編集処理
     */
    @PutMapping("/updateComment/{id}")
    public ModelAndView updateComment(@PathVariable Integer id, @ModelAttribute("commentForm") @Validated CommentForm comment, BindingResult result) {
        if (result.hasErrors()) {
            // バリデーションエラー時、edit画面へ戻す
            return new ModelAndView("/editComment");
        }
        // UrlParameterのidを更新するentityにセット
        comment.setId(id);
        // 編集した投稿を更新
        commentService.saveComment(comment);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント削除処理
     */
    @DeleteMapping("/deleteComment/{id}")
    public ModelAndView deleteComment(@PathVariable Integer id) {
        //投稿をテーブルに格納
        commentService.deleteComment(id);
        //rootへリダイレクト
        return new ModelAndView("redirect:/");
    }
}
