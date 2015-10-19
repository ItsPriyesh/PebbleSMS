#include <pebble.h>

static Window *window;
static TextLayer *text_layer;
static SimpleMenuLayer *menu_layer;

static DictationSession *s_dictation_session;
static char s_dictation_text[512];

static char * names[5];
static int num_names = 0;
static int name_selected = -1;

// Dictation
static void dictation_session_callback(DictationSession *session,
                                       DictationSessionStatus status, char *transcription,
                                       void *context) {
  DictionaryIterator *iter;
  app_message_outbox_begin(&iter);
  dict_write_cstring(iter, 0, names[name_selected]);
  dict_write_cstring(iter, 1, transcription);
  app_message_outbox_send();
}

// Menu stuff
static void menu_item_click(int index, void *context) {
  name_selected = index;
  dictation_session_start(s_dictation_session);
}

static void setup_menu() {
  Layer *window_layer = window_get_root_layer(window);
  GRect bounds = layer_get_bounds(window_layer);
  SimpleMenuItem items[5];
  int num_items = 0;
  for (int i = 0; i < 5; i++) {
    if (!strlen(names[i])) {
      num_items = i;
      break;
    }

    items[i] = (SimpleMenuItem) {
      .title = names[i],
      .callback = menu_item_click
    };
  }
  const SimpleMenuSection section = (SimpleMenuSection) {
    .title = "Contacts",
    .items = items,
    .num_items = num_items
  };

  menu_layer = simple_menu_layer_create(bounds, window, &section, 5, NULL);
  layer_add_child(window_layer, simple_menu_layer_get_layer(menu_layer));
}

// Click handlers
static void select_click_handler(ClickRecognizerRef recognizer, void *context) {
}

static void up_click_handler(ClickRecognizerRef recognizer, void *context) {
}

static void down_click_handler(ClickRecognizerRef recognizer, void *context) {
}

static void click_config_provider(void *context) {
  window_single_click_subscribe(BUTTON_ID_SELECT, select_click_handler);
  window_single_click_subscribe(BUTTON_ID_UP, up_click_handler);
  window_single_click_subscribe(BUTTON_ID_DOWN, down_click_handler);
}

// App message callbacks
static void inbox_received_callback(DictionaryIterator *iterator, void *context) {
  APP_LOG(APP_LOG_LEVEL_INFO, "Message received!");
  Tuple *data = dict_read_first(iterator);
  while (data) {
    names[num_names] = malloc(strlen((char *) data->value));
    names[num_names++] = (char *) data->value;
    data = dict_read_next(iterator);
  }

  if (num_names == 5) {
    setup_menu();
  }
}

static void inbox_dropped_callback(AppMessageResult reason, void *context) {
  APP_LOG(APP_LOG_LEVEL_ERROR, "Message dropped!");
}

static void outbox_failed_callback(DictionaryIterator *iterator, AppMessageResult reason, void *context) {
  APP_LOG(APP_LOG_LEVEL_ERROR, "Outbox send failed!");
}

static void outbox_sent_callback(DictionaryIterator *iterator, void *context) {
  APP_LOG(APP_LOG_LEVEL_INFO, "Outbox send success!");
}

static void window_load(Window *window) {
  Layer *window_layer = window_get_root_layer(window);
  GRect bounds = layer_get_bounds(window_layer);

  /* text_layer = text_layer_create((GRect) { .origin = { 0, 72 }, .size = { bounds.size.w, 20 } }); */
  /* text_layer_set_text(text_layer, "Press a button"); */
  /* text_layer_set_text_alignment(text_layer, GTextAlignmentCenter); */
  /* layer_add_child(window_layer, text_layer_get_layer(text_layer)); */
}

static void window_unload(Window *window) {
  text_layer_destroy(text_layer);
}

static void init(void) {
  // dicatation session
  s_dictation_session = dictation_session_create(sizeof(s_dictation_text),
                                                 dictation_session_callback, NULL);

  // app message
  app_message_register_inbox_received(inbox_received_callback);
  app_message_register_inbox_dropped(inbox_dropped_callback);
  app_message_register_outbox_failed(outbox_failed_callback);
  app_message_register_outbox_sent(outbox_sent_callback);
  app_message_open(app_message_inbox_size_maximum(), app_message_outbox_size_maximum());

  window = window_create();
  window_set_click_config_provider(window, click_config_provider);
  window_set_window_handlers(window, (WindowHandlers) {
    .load = window_load,
    .unload = window_unload,
  });
  const bool animated = true;
  window_stack_push(window, animated);
}

static void deinit(void) {
  window_destroy(window);
}

int main(void) {
  init();

  APP_LOG(APP_LOG_LEVEL_DEBUG, "Done initializing, pushed window: %p", window);

  app_event_loop();
  deinit();
}
