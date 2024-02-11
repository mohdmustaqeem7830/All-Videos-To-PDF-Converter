package com.allVideosTo.PdfConverter7830;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.PdfViewHolder> {
    private static ArrayList<File> pdfFiles;
    private Dialog renameDialog;
    private  AlertDialog.Builder alertDialog;

    private int selectedPosition = -1;
    Context context;
    private ArrayList<Integer> selectedItems = new ArrayList<>();
    public interface OnItemClickListener {
        void onItemClick(String pdfFilePath);
    }

    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position ;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);

        void onItemClick(int position);

        void onItemClick(String pdfFilePath);

//        void onItemClick(String pdfFilePath);
    }

    private OnItemLongClickListener longClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public PdfAdapter(ArrayList<File> pdfFiles,Context context )
    {
        this.pdfFiles = pdfFiles;
        this.context = context;


    }

    @NonNull
    @Override
    public PdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_item, parent, false);
        return new PdfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfViewHolder holder, @SuppressLint("RecyclerView") int position) {
        File pdfFile = pdfFiles.get(position);
        holder.pdfFileName.setText(pdfFile.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(pdfFiles.get(position).getAbsolutePath());
                }
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(position);
            }
        });
        holder.rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRenameDialog(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pdfFiles.size();
    }


    static class PdfViewHolder extends RecyclerView.ViewHolder {
        TextView pdfFileName;
        CardView cardView;

        ImageView delete,rename;

        PdfViewHolder(@NonNull View itemView) {
            super(itemView);
            pdfFileName = itemView.findViewById(R.id.pdfFileName);
            cardView =itemView.findViewById(R.id.cardbackground);
            delete = itemView.findViewById(R.id.delete);
            rename = itemView.findViewById(R.id.rename);
        }
    }
    public void deletePdf(int position) {
        if (position >= 0 && position < pdfFiles.size()) {
            File pdfFile = pdfFiles.get(position);
            if (pdfFile != null) {
                if (pdfFile.delete()) {
                    // Successfully deleted the file from storage
                    pdfFiles.remove(position);
                    notifyItemRemoved(position);
                    notifyItemChanged(position);
                    notifyDataSetChanged();
                }
            }
        }
    }
    private void showRenameDialog(int position) {
        renameDialog =  new Dialog(context);
        renameDialog.setContentView(R.layout.rename) ;
        renameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = renameDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Set width to match parent
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Set height as needed
            window.setAttributes(layoutParams);
        }
        Button buttonRename = renameDialog.findViewById(R.id.btnrename);
        Button buttonCancel = renameDialog.findViewById(R.id.btncancel);
        EditText newname = renameDialog.findViewById(R.id.renametext);

        // Set a click listener for the "Cancel" button within the dialog
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renameDialog.dismiss();
            }
        });

        // Set a click listener for the "Rename" button within the dialog
        buttonRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement your rename logic here
                // ...
                String newFileName = newname.getText().toString();
                newFileName = generateUniqueName(newFileName);
                renameSelectedPDF(position,newFileName);
               notifyDataSetChanged();
                // Dismiss the dialog
                renameDialog.dismiss();
            }
        });


        renameDialog.show();
    }
    private void renameSelectedPDF(int position, String newFileName) {
        if (position >= 0 && position < pdfFiles.size()) {
            File selectedFile = pdfFiles.get(position);
            String oldPath = selectedFile.getAbsolutePath();

            // Construct the new path with the updated file name
            File newFile = new File(selectedFile.getParent(), newFileName + ".pdf");
            String newPath = newFile.getAbsolutePath();

            // Rename the file in storage
            if (selectedFile.renameTo(newFile)) {
                pdfFiles.set(position, newFile);
               notifyDataSetChanged();
            }
        }
    }

    private String generateUniqueName(String baseName) {
        String newName = baseName;
        int suffix = 1;

        File directory = new File(Environment.getExternalStorageDirectory(), "Videos To PDF");

        // Check if a file with the generated name already exists
        while (new File(directory, newName + ".pdf").exists()) {
            // If it does, add a number suffix
            newName = baseName + "_" + suffix;
            suffix++;
        }

        return newName;
    }
    //code for delete the pdf
    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete PDF");
        builder.setMessage("Are you sure want to delete this PDF file?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User confirmed to delete
                        deletePdf(position);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User canceled the delete operation

                    }
                });

        builder.create().show();
    }

}
