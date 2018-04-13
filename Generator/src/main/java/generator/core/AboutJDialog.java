package generator.core;

import generator.common.tools.Constants;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.util.ResourceBundle;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

public class AboutJDialog extends JDialog
{
	private ResourceBundle resourceBundle;
	private AboutJDialog thisDialog;
	private int dialogWidth = 400;
	private int dialogHeight = 300;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton closeButton = new JButton();
	
	public AboutJDialog(Frame frame, boolean modal, ResourceBundle resourceBundle)
	{
		super(frame,modal);
		thisDialog = this;
		this.resourceBundle = resourceBundle;
		this.setTitle(resourceBundle.getString("aboutDialog.title"));
		closeButton.setText(resourceBundle.getString("closeDialog"));
		
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		javax.swing.JLabel appTitleLabel = new javax.swing.JLabel();
		javax.swing.JLabel versionLabel = new javax.swing.JLabel();
		javax.swing.JLabel appVersionLabel = new javax.swing.JLabel();
		javax.swing.JLabel vendorLabel = new javax.swing.JLabel();
		javax.swing.JLabel appVendorLabel = new javax.swing.JLabel();
		javax.swing.JLabel homepageLabel = new javax.swing.JLabel();
		javax.swing.JLabel appHomepageLabel = new javax.swing.JLabel();
		javax.swing.JLabel appDescLabel = new javax.swing.JLabel();
		javax.swing.JLabel imageLabel = new javax.swing.JLabel();
		setResizable(false);
		
		appTitleLabel.setFont(Constants.DEFAULT_WIN_FONT);
		appTitleLabel.setFont(appTitleLabel.getFont().deriveFont(
			appTitleLabel.getFont().getStyle() | java.awt.Font.BOLD, appTitleLabel.getFont().getSize() + 4));
		appTitleLabel.setText(resourceBundle.getString("Application.title")); // NOI18N
		appTitleLabel.setName("appTitleLabel"); // NOI18N

		versionLabel.setFont(Constants.DEFAULT_WIN_FONT);
		versionLabel.setFont(versionLabel.getFont().deriveFont(versionLabel.getFont().getStyle() | java.awt.Font.BOLD));
		versionLabel.setText(resourceBundle.getString("versionLabel.text")); // NOI18N
		versionLabel.setName("versionLabel"); // NOI18N

		appVersionLabel.setFont(Constants.DEFAULT_WIN_FONT);
		appVersionLabel.setText(resourceBundle.getString("Application.version")); // NOI18N
		appVersionLabel.setName("appVersionLabel"); // NOI18N

		vendorLabel.setFont(Constants.DEFAULT_WIN_FONT);
		vendorLabel.setFont(vendorLabel.getFont().deriveFont(vendorLabel.getFont().getStyle() | java.awt.Font.BOLD));
		vendorLabel.setText(resourceBundle.getString("vendorLabel.text")); // NOI18N
		vendorLabel.setName("vendorLabel"); // NOI18N

		appVendorLabel.setFont(Constants.DEFAULT_WIN_FONT);
		appVendorLabel.setText(resourceBundle.getString("Application.vendor")); // NOI18N
		appVendorLabel.setName("appVendorLabel"); // NOI18N

		homepageLabel.setFont(Constants.DEFAULT_WIN_FONT);
		homepageLabel.setFont(homepageLabel.getFont().deriveFont(
			homepageLabel.getFont().getStyle() | java.awt.Font.BOLD));
		homepageLabel.setText(resourceBundle.getString("homepageLabel.text")); // NOI18N
		homepageLabel.setName("homepageLabel"); // NOI18N

		appHomepageLabel.setFont(Constants.DEFAULT_WIN_FONT);
		appHomepageLabel.setText(resourceBundle.getString("Application.homepage")); // NOI18N
		appHomepageLabel.setName("appHomepageLabel"); // NOI18N

		appDescLabel.setFont(Constants.DEFAULT_WIN_FONT);
		appDescLabel.setText(resourceBundle.getString("appDescLabel.text")); // NOI18N
		appDescLabel.setName("appDescLabel"); // NOI18N
		ImageIcon logoImage = new ImageIcon(getClass().getClassLoader().getResource("logo.PNG"));
//		 ImageFilter filter = new RGBImageFilter() {
//	         int transparentColor = Color.white.getRGB() | 0xFF000000;
//
//	         public final int filterRGB(int x, int y, int rgb) {
//	            if ((rgb | 0xFF000000) == transparentColor) {
//	               return 0x00FFFFFF & rgb;
//	            } else {
//	               return rgb;
//	            }
//	         }
//	      };
//
//	      ImageProducer filteredImgProd = new FilteredImageSource(logoImage.getImage().getSource(), filter);
//	      Image transparentImg = Toolkit.getDefaultToolkit().createImage(filteredImgProd);
//	      
		imageLabel.setIcon(logoImage);
//		
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			layout
			.createSequentialGroup()
			.addComponent(imageLabel)
			.addGap(18, 18, 18)
			.addGroup(
				layout
					.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
					.addGroup(
						javax.swing.GroupLayout.Alignment.LEADING,
						layout
							.createSequentialGroup()
							.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
									.addComponent(versionLabel).addComponent(vendorLabel)
									.addComponent(homepageLabel))
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
									.addComponent(appVersionLabel).addComponent(appVendorLabel)
									.addComponent(appHomepageLabel)))
					.addComponent(appTitleLabel, javax.swing.GroupLayout.Alignment.LEADING)
					.addComponent(appDescLabel, javax.swing.GroupLayout.Alignment.LEADING,
						javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE).addComponent(closeButton))
			.addContainerGap()));
	layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
		layout
			.createSequentialGroup()
			.addGroup(
				layout
					.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addGroup(
						layout
							.createSequentialGroup()
							.addContainerGap()
							.addComponent(appTitleLabel)
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(appDescLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
									.addComponent(versionLabel).addComponent(appVersionLabel))
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
									.addComponent(vendorLabel).addComponent(appVendorLabel))
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
									.addComponent(homepageLabel).addComponent(appHomepageLabel))
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16,
								Short.MAX_VALUE).addComponent(closeButton)).addComponent(imageLabel))
			.addContainerGap()));

	
		
		closeButton.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
//		thisDialog.setVisible(false);
				thisDialog.dispose();
			}
		});
//		this.setSize(300, 200);
		int positionX = frame.getX() + ((frame.getWidth() - dialogWidth)/2);
		int positionY = frame.getY() + ((frame.getHeight() - dialogHeight)/2);
		this.setBounds(positionX, positionY, dialogWidth, dialogHeight);
		
		pack();
	}
	
	
	

}
