package pcd.ass_single.part2.rmi;

public abstract class AbstractRemoteEventHandler implements RemoteEventHandler{
    protected final BrushManager brushManager;
    protected final PixelGridView view;
    protected final PixelGrid grid;

    protected AbstractRemoteEventHandler(BrushManager brushManager, PixelGridView view, PixelGrid grid) {
        this.brushManager = brushManager;
        this.view = view;
        this.grid = grid;
    }

    @Override
    public void onBrushAdded(BrushDTO brushDTO) {
        brushManager.addBrush(brushDTO.getPeerId(), new BrushManager.Brush(brushDTO.getX(), brushDTO.getY(), brushDTO.getColor()));
        view.refresh();
    }

    @Override
    public void onBrushMoved(BrushDTO brushDTO) {
        log(brushDTO.getPeerId() + " move to " + brushDTO.getX() + ", " + brushDTO.getY());
        if (brushManager.containsBrush(brushDTO.getPeerId())) {
            brushManager.updateBrushPosition(brushDTO.getPeerId(), brushDTO.getX(), brushDTO.getY());
            log("try to update new brush: " + brushDTO.getPeerId());
        } else {
            log("brush was not there: " + brushDTO.getPeerId());
            brushManager.addBrush(brushDTO.getPeerId(), new BrushManager.Brush(brushDTO.getX(), brushDTO.getY(), brushDTO.getColor()));
        }
        view.refresh();
    }

    @Override
    public void onBrushColorChanged(BrushDTO brushDTO) {
        brushManager.updateBrushColor(brushDTO.getPeerId(), brushDTO.getColor());
        view.refresh();
    }

    @Override
    public void onPixelDrawn(BrushDTO brushDTO) {
        grid.set(brushDTO.getX(), brushDTO.getY(), brushDTO.getColor());
        view.refresh();
    }

    @Override
    public void onBrushRemoved(Integer id) {
        brushManager.removeBrush(id);
        view.refresh();
    }

    private static void log(String msg) {
        System.out.println(msg);
    }

}
