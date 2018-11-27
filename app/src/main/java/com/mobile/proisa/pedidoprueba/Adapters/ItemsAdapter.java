package com.mobile.proisa.pedidoprueba.Adapters;


public class ItemsAdapter {

}
/*
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyHolder>{
    public static final String TAG = "ItemsAdapter";
    private List<Item> itemList;
    private int layoutResource;

    private MyItemClick myItemClick;


    public ItemsAdapter(List<Item> itemList, int layoutResource) {
        this.itemList = itemList;
        this.layoutResource = layoutResource;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(layoutResource, parent, false);
        MyHolder holder = new MyHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        final Item item = itemList.get(position);

        holder.txtNombre.setText(item.getName());
        holder.txtId.setText(item.getId());
        holder.txtCantidad.setText(NumberUtils.formatNumber(item.getQuantity(), NumberUtils.FORMAT_NUMER_DOUBLE));
        holder.txtCantidadInv.setText(NumberUtils.formatNumber(item.getStock(), NumberUtils.FORMAT_NUMER_DOUBLE));
        holder.txtPrecio.setText(NumberUtils.formatNumber(item.getPrice(), NumberUtils.FORMAT_NUMER_DOUBLE));
        holder.txtSubtotal.setText(NumberUtils.formatNumber(item.getTotal(), NumberUtils.FORMAT_NUMER_DOUBLE));


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myItemClick != null) myItemClick.onItemClickListener(item, position);
            }
        });


        holder.toolbar.setOnMenuItemClickListener(new MyMenuClickListener(position, new OnNotifyNeededListener() {

            @Override
            public void update(int position, double newQuantity) {
                Log.d(TAG, "postion to update: "+position + " with value "+newQuantity);
                Item item = itemList.get(position);
                item.setQuantity(item.getQuantity() + newQuantity);
                notifyItemChanged(position);
            }

            @Override
            public void onDelete(int position) {
                Log.d(TAG, "postion to Delete: "+position);
                itemList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        }));
    }

    public void setMyItemClick(MyItemClick myItemClick) {
        this.myItemClick = myItemClick;
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public long getItemId(int position) {
        return itemList.get(position).hashCode();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView txtNombre;
        public TextView txtId;
        public TextView txtCantidad;
        public TextView txtCantidadInv;
        public TextView txtPrecio;
        public TextView txtSubtotal;
        public Toolbar toolbar;
        public CardView cardView;

        public MyHolder(View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.nombre);
            txtId = itemView.findViewById(R.id.id);
            txtCantidad = itemView.findViewById(R.id.cantidad);
            txtCantidadInv = itemView.findViewById(R.id.cantidad_inventario);
            txtPrecio = itemView.findViewById(R.id.precio);
            txtSubtotal = itemView.findViewById(R.id.subtotal);
            toolbar = itemView.findViewById(R.id.toolbarCard);
            toolbar.inflateMenu(R.menu.menu_per_item);

            cardView = itemView.findViewById(R.id.card);

        }
    }

    public static class MyMenuClickListener implements  Toolbar.OnMenuItemClickListener{
        private int position;
        private OnNotifyNeededListener notifyNeededListener;

        public MyMenuClickListener(int position, OnNotifyNeededListener notifyNeededListener) {
            this.position = position;
            this.notifyNeededListener = notifyNeededListener;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()){
                case R.id.action_add:
                   notifyNeededListener.update(position, 1);
                    break;

                case R.id.action_remove:
                    notifyNeededListener.update(position, -1);
                    break;

                case R.id.action_delete:
                    notifyNeededListener.onDelete(position);
                    break;


            }

            return true;
        }

        public interface OnNotifyNeededListener{
            public void update(int position, double newQuantity);
            public void onDelete(int position);

        }
    }

    public interface MyItemClick{
        void onItemClickListener(Object item, int position);
    }

}
*/