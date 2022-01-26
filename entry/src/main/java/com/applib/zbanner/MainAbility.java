package com.applib.zbanner;


import com.example.zbanner.ResourceTable;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.BaseItemProvider;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.LayoutScatter;
import ohos.agp.components.ListContainer;
import ohos.agp.components.Text;
import zhuang.zbanner.util.LogUtil;
import java.util.List;

/**
 *  MainAbility.
 */
public class MainAbility extends Ability implements ListContainer.ItemClickedListener {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);


        ListContainer listView = (ListContainer) findComponentById(ResourceTable.Id_listView);
        final List<ItemsSources.ExampleItem> list = ItemsSources.getItems();
        MainListProvider adapter = new MainListProvider(list);
        listView.setItemProvider(adapter);
        listView.setItemClickedListener(this);

    }

    /**
     * The type Main list provider.
     */
    public class MainListProvider extends BaseItemProvider {
        /**
         * The List items.
         */
        List<ItemsSources.ExampleItem> listItems;

        /**
         * Instantiates a new Main list provider.
         *
         * @param lst the lst
         */
        MainListProvider(List<ItemsSources.ExampleItem> lst) {
            listItems = lst;
        }

        @Override
        public int getCount() {
            return listItems.size();
        }

        @Override
        public Object getItem(int position) {
            return listItems.get(position).title;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Component getComponent(int position, Component component, ComponentContainer componentContainer) {
            Component convertView = component;
            if (convertView == null) {
                convertView = LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_simple_list_item_1,
                        componentContainer, false);
            }
            ((Text) (convertView.findComponentById(ResourceTable.Id_list_component))).setText(
                    (String) getItem(position)
            );
            convertView.setClickable(false);
            return convertView;
        }
    }

    @Override
    public void onItemClicked(ListContainer listContainer, Component component, int i, long l) {
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withAction("action.banner")
                .build();

        LogUtil.info("check", i + "");

        intent.setParam("position", i);
        intent.setOperation(operation);
        startAbility(intent);
    }
}
