package entrance.ui;

import business.action.MiaoBoLiveAction;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MiaoBoLiveGUI extends Application {

	/*
	 * start方法在于开始运行时要做的事情，通常初始化组件的
	 */
	@Override
	public void start(Stage primaryStage) {
		System.out.println("程序启动！");
		primaryStage.setAlwaysOnTop(true);
		try {
			init(primaryStage);
		} catch (Exception e) {
			System.out.println("自动注册出错！");
			e.printStackTrace();
		}
	}

	/*
	 * stop方法在于结束运行时要做的事情，如何收尾
	 */
	@Override
	public void stop() throws Exception {
		System.out.println("程序关闭！");
	}

	/*
	 * 初始化工作
	 */
	private void init(Stage primaryStage) throws Exception {
		// Stage类是顶级JavaFX容器。初级阶段是由平台构建的。应用程序可以构造其他阶段对象。
		primaryStage.setTitle("喵播");
		Label titleLabel = new Label("实时状态：");
		HBox titleBox = new HBox();
		Label contentLabel = new Label("");
		HBox contentBox = new HBox();
		titleBox.getChildren().addAll(titleLabel);
		contentBox.getChildren().addAll(contentLabel);
		// labelBox.setAlignment(Pos.TOP_CENTER);
		titleBox.setAlignment(Pos.TOP_CENTER);
		contentBox.setAlignment(Pos.CENTER);

		// 布局类
		StackPane root = new StackPane();
		Scene scene = new Scene(root, 300, 250);
		primaryStage.setScene(scene);
		primaryStage.show();

		// 注册按钮
		Button signUpBtn = new Button("访问下载开始");
		// 暂停按钮
		Button cancelBtn = new Button("暂停");
		// 布局
		HBox btnBox = new HBox();
		btnBox.setSpacing(10);
		btnBox.getChildren().addAll(signUpBtn, cancelBtn);
		btnBox.setAlignment(Pos.BOTTOM_CENTER);

		// 注册按钮事件
		signUpBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				contentLabel.setText("自动注册按钮被点击！");
				MiaoBoLiveAction happy88Action = new MiaoBoLiveAction();
				happy88Action.downLoadAppLimit();
				signUpBtn.setDisable(true);
				cancelBtn.setDisable(false);
			}
		});

		// 暂停按钮事件
		cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				contentLabel.setText("暂停按钮被点击！");
				cancelBtn.setDisable(true);
				signUpBtn.setDisable(false);
			}
		});

		root.getChildren().add(0, titleBox);
		root.getChildren().add(1, contentBox);
		root.getChildren().add(2, btnBox);

	}

	/*
	 * run是启动程序
	 */
	public void run(String[] args) {
		launch(args);
	}

}
