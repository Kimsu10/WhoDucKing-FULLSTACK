package hello.controller;

import hello.dto.board.BoardDTO;
import hello.entity.board.Board;
import hello.service.BoardService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public String freeBoard(Model model) {
        List<Board> boardList = boardService.getAllBoards();
        model.addAttribute("boardList", boardList);

        return "board/freeBoard";
    }

    //freeBoard -> 작성 폼을 띄워주는 역할
    @GetMapping("/new")
    public String showCreateBoard(Model model) {
        model.addAttribute("board", new BoardDTO());
        return "board/create";
    }

    //작성된 폼을 가지고 새로운 게시글 작성
    @PostMapping("/create")
    public String createBoard(@ModelAttribute("board") BoardDTO board) {
        boardService.createBoard(board);
        return "redirect:/board";
    }

    //목록에서 해당 id에 맞는 게시글 상세보기
    @GetMapping("/{boardId}")
    public String showBoard(@PathVariable("boardId") Long boardId, Model model) {
        Board board = boardService.getBoardById(boardId).orElse(null);

        //null일경우 페이지 이동안함
        if(board == null) {
            return "redirect:/board";
        }
        model.addAttribute("board", board);
        return "board/show";
    }

    //게시글 수정 폼
    @GetMapping("/{boardId}/edit")
    public String showEditBoard(@PathVariable("boardId") Long boardId, Model model) {
        Board board = boardService.getBoardById(boardId).orElse(null);
        if(board == null) {
            return "redirect:/board";
        }
        model.addAttribute("board", board);
        return "board/edit";
    }

    @PostMapping("/{boardId}/edit")
    public String editBoard(@PathVariable("boardId") Long boardId, @ModelAttribute("updatedBoard") BoardDTO updatedBoard) throws NotFoundException {
        boardService.updateBoard(boardId,updatedBoard);
        return "redirect:/board";
    }

    @PostMapping("/{boardId}/delete")
    public String deleteBoard(@PathVariable("boardId") Long boardId) throws NotFoundException {
        boardService.deleteBoard(boardId);
        return "redirect:/board";
    }

}
