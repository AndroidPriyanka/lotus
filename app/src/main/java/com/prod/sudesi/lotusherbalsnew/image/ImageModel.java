package com.prod.sudesi.lotusherbalsnew.image;

public class ImageModel {

		private String path;
		private int selectedImg;
		  private boolean selected;

		  public ImageModel(String path,int selectedImg) {
		    this.path = path;
		    this.selectedImg = selectedImg;
		    selected = false;
		  }

		  public String getPath() {
		    return path;
		  }

		  public void setPath(String path) {
		    this.path = path;
		  }
		  
		  public int getImg() {
			    return selectedImg;
			  }

			  public void setImg(int selectedImg) {
			    this.selectedImg = selectedImg;
			  }


		  public boolean isSelected() {
		    return selected;
		  }

		  public void setSelected(boolean selected) {
		    this.selected = selected;
		  }

}