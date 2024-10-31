package com.example.helloworld;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {

    private List<BoardCell> boardCells;

    public BoardAdapter(List<BoardCell> boardCells) {
        this.boardCells = boardCells;
    }

    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_board, parent, false);
        return new BoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {
        BoardCell cell = boardCells.get(position);
        holder.bind(cell);
    }

    @Override
    public int getItemCount() {
        return boardCells.size();
    }

    static class BoardViewHolder extends RecyclerView.ViewHolder {
        private TextView boardCell;

        public BoardViewHolder(@NonNull View itemView) {
            super(itemView);
            boardCell = itemView.findViewById(R.id.boardCell);
        }

        public void bind(BoardCell cell) {
            boardCell.setText(cell.getLabel());
        }
    }
}
