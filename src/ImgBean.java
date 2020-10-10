public class ImgBean {
    private String image;
    private String image_type;
    private String face_type;
    private String quality_control;
    private String liveness_control;

    public ImgBean(String image, String image_type, String face_type, String quality_control, String liveness_control) {
        this.image = image;
        this.image_type = image_type;
        this.face_type = face_type;
        this.quality_control = quality_control;
        this.liveness_control = liveness_control;
    }
}
