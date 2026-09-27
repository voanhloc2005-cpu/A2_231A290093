package vn.edu.vhu.ltdd.a2_231a290093;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // =========================
    // BIẾN TRẠNG THÁI ĐỒNG HỒ
    // =========================

    private boolean running = false;

    // Tổng thời gian của các lần chạy trước
    private long accumulated = 0;

    // Thời điểm bắt đầu lần chạy hiện tại
    private long startTime = 0;

    // Số lần Activity được tạo lại
    private int recreateCount = 0;


    // =========================
    // CÁC THÀNH PHẦN GIAO DIỆN
    // =========================

    private TextView tvTime;
    private TextView tvStatus;
    private TextView tvRecreate;

    private Button btnStartPause;
    private Button btnReset;


    // =========================
    // HANDLER + TICKER
    // =========================

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private final Runnable ticker = new Runnable() {

        @Override
        public void run() {

            updateTimeText();

            // 100 mili-giây cập nhật một lần
            handler.postDelayed(this, 100);
        }
    };


    // =========================
    // ON CREATE
    // =========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        // Activity được tạo lại
        recreateCount++;


        // =========================
        // ÁNH XẠ VIEW
        // =========================

        tvTime = findViewById(R.id.tvTime);

        tvStatus = findViewById(R.id.tvStatus);

        tvRecreate = findViewById(R.id.tvRecreate);

        btnStartPause = findViewById(R.id.btnStartPause);

        btnReset = findViewById(R.id.btnReset);


        // Hiển thị số lần Activity được tạo lại
        tvRecreate.setText(
                getString(
                        R.string.recreate_count,
                        recreateCount
                )
        );


        // =========================
        // NÚT BẮT ĐẦU / TẠM DỪNG
        // =========================

        btnStartPause.setOnClickListener(v -> {

            if (!running) {

                // -------------------------
                // BẮT ĐẦU CHẠY
                // -------------------------

                running = true;

                startTime = SystemClock.elapsedRealtime();

                btnStartPause.setText(R.string.pause);

                tvStatus.setText(R.string.status_running);

                startTicking();

            } else {

                // -------------------------
                // TẠM DỪNG
                // -------------------------

                accumulated = elapsed();

                running = false;

                btnStartPause.setText(R.string.start);

                tvStatus.setText(R.string.status_paused);

                stopTicking();

                updateTimeText();
            }
        });


        // =========================
        // NÚT ĐẶT LẠI
        // =========================

        btnReset.setOnClickListener(v -> {

            running = false;

            accumulated = 0;

            startTime = 0;

            btnStartPause.setText(R.string.start);

            tvStatus.setText(R.string.status_paused);

            stopTicking();

            updateTimeText();
        });


        // Hiển thị thời gian ban đầu
        updateTimeText();
    }


    // =========================
    // TÍNH THỜI GIAN ĐÃ TRÔI QUA
    // =========================

    private long elapsed() {

        if (!running) {
            return accumulated;
        }

        return accumulated
                + (SystemClock.elapsedRealtime() - startTime);
    }


    // =========================
    // CẬP NHẬT THỜI GIAN LÊN MÀN HÌNH
    // =========================

    private void updateTimeText() {

        long ms = elapsed();

        long minutes = ms / 60000;

        long seconds = (ms / 1000) % 60;

        long tenths = (ms / 100) % 10;


        String time = String.format(
                Locale.getDefault(),
                "%02d:%02d.%d",
                minutes,
                seconds,
                tenths
        );


        tvTime.setText(time);
    }


    // =========================
    // BẮT ĐẦU TICKER
    // =========================

    private void startTicking() {

        // Xóa ticker cũ trước
        // để tránh chạy chồng
        handler.removeCallbacks(ticker);

        handler.post(ticker);
    }


    // =========================
    // DỪNG TICKER
    // =========================

    private void stopTicking() {

        handler.removeCallbacks(ticker);
    }


    // =========================
    // ACTIVITY BỊ HỦY
    // =========================

    @Override
    protected void onDestroy() {

        // Không để Handler tiếp tục chạy
        handler.removeCallbacks(ticker);

        super.onDestroy();
    }
}
