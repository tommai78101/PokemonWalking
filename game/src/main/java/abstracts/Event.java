package abstracts;

public abstract class Event {
	protected Entity interactor;
	protected Entity interactee;

	public void setInstigator(Entity entity) {
		this.interactor = entity;
	}

	public Entity getInstigator() {
		return this.interactor;
	}

	public void setTarget(Entity entity) {
		this.interactee = entity;
	}

	public Entity getTarget() {
		return this.interactee;
	}
}
