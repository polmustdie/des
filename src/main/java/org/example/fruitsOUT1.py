import os
# os.environ["CUDA_VISIBLE_DEVICES"]="-1"   
import matplotlib.pyplot as plt
import numpy as np
import tensorflow as tf
from sklearn.datasets import load_files

import cv2 as cv
from tensorflow.python.keras import models
from tensorflow.python.keras.models import Sequential
from tensorflow.python.keras.layers import Conv2D, MaxPooling2D
from tensorflow.python.keras.layers import Activation, Dense, Flatten, Dropout
from keras.preprocessing.image import img_to_array, load_img
from keras.utils import np_utils
from PIL import Image


pathToData = './data/fruits-360_dataset/fruits-360/Training/'
pathToTest = './data/fruits-360_dataset/fruits-360/Test/'

def load_dataset(data_path):
    data_loading = load_files(data_path)
    files_add = np.array(data_loading['filenames'])
    target_fruits = np.array(data_loading['target'])
    target_labels_fruits = np.array(data_loading['target_names'])
    return files_add, target_fruits, target_labels_fruits

    
x_train, y_train, target_labels = load_dataset(pathToData)
x_test, y_test, _ = load_dataset(pathToTest)

no_of_classes = len(np.unique(y_train))

y_train = np_utils.to_categorical(y_train, no_of_classes)
y_test = np_utils.to_categorical(y_test, no_of_classes)

x_test, x_valid = x_test[300:], x_test[:300]
y_test, y_vaild = y_test[300:], y_test[:300]


def convert_image_to_array_form(files):
    images_array = []
    for file in files:
        images_array.append(img_to_array(load_img(file)))
    return images_array

x_train = np.array(convert_image_to_array_form(x_train))

x_valid = np.array(convert_image_to_array_form(x_valid))

x_test = np.array(convert_image_to_array_form(x_test))

x_train = x_train.astype('float32') / 255
x_valid = x_valid.astype('float32') / 255
x_test = x_test.astype('float32') / 255


shape_of_img = 100
number_colors = 3

# def tensorflow_based_model():
#     model = Sequential() #step 1
#     model.add(Conv2D(filters = 16, kernel_size = 2,input_shape=(100,100,3),padding='same')) #step2
#     model.add(Activation('relu'))  # step3
#     model.add(MaxPooling2D(pool_size=2)) #step4
#     model.add(Conv2D(filters = 32,kernel_size = 2,activation= 'relu',padding='same')) #repeating step 2 and step3 but with more filters of 32
#     model.add(MaxPooling2D(pool_size=2)) #repeating step 4 again
#     model.add(Conv2D(filters = 64,kernel_size = 2,activation= 'relu',padding='same')) #repeating step 2 and step3 but with more filters of 64
#     model.add(MaxPooling2D(pool_size=2)) #repeating step 4 again
#     model.add(Conv2D(filters = 128,kernel_size = 2,activation= 'relu',padding='same')) #repeating step 2 and step3 but with more filters of 64
#     model.add(MaxPooling2D(pool_size=2)) #repeating step 4 again
#     model.add(Dropout(0.3)) # step5
#     model.add(Flatten()) #step 6
#     model.add(Dense(150)) #step 7
#     model.add(Activation('relu')) # setp 3
#     model.add(Dropout(0.4)) # step 5
#     model.add(Dense(4,activation = 'softmax')) # setp3 and step7. but this time, we are using activation function as softmax (if we train on two classes then we set sigmoid)
#     return model #function returning the value when we call it

# model = tensorflow_based_model() # here we are calling the function of created model
# model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy']) 

# history = model.fit(x_train, y_train,
#         batch_size=32,
#         epochs=8,
#         validation_data=(x_valid, y_vaild),
#         verbose=2, shuffle=True)

# model.save('./fruit_classifaer.model')

# acc_score = model.evaluate(x_test, y_test) #we are starting to test the model here
# print('\n', 'Test accuracy:', acc_score[1])

# predictions = model.predict(x_test)
# fig = plt.figure(figsize=(16, 9))
# for i, idx in enumerate(np.random.choice(x_test.shape[0], size=16, replace=False)):
#     ax = fig.add_subplot(4, 4, i + 1, xticks=[], yticks=[])
#     ax.imshow(np.squeeze(x_test[idx]))
#     pred_idx = np.argmax(predictions[idx])
#     true_idx = np.argmax(y_test[idx])
#     ax.set_title("{} ({})".format(target_labels[pred_idx], target_labels[true_idx]),
#                  color=("green" if pred_idx == true_idx else "red"))
    
# plt.figure(1)  

# plt.subplot(211)
# plt.plot(history.history['accuracy'])
# plt.plot(history.history['val_accuracy'])
# plt.title('model accuracy')  
# plt.ylabel('accuracy')  
# plt.xlabel('epoch')  
# plt.legend(['train', 'test'], loc='upper left') 


# plt.subplot(212)  
# plt.plot(history.history['loss'])  
# plt.plot(history.history['val_loss'])  
# plt.title('model loss')  
# plt.ylabel('loss')  
# plt.xlabel('epoch')  
# plt.legend(['train', 'test'], loc='upper left')  
# plt.show()


model_load = models.load_model('./fruit_classifaer.model')


img = cv.imread('./data/my_test/orange.jpg')
img = Image.fromarray(img, 'RGB')
image = img.resize((shape_of_img, shape_of_img))
img = np.array(image) / 255
a = []
a.append(img)
a = np.array(a)

prediction = model_load.predict(a, verbose=1)
index = np.argmax(prediction)
# print(index)
# print(target_labels)
print("Your image {} is type {}".format('orange.jpg', target_labels[index]))

img = cv.imread('./data/my_test/banan.jpeg')
img = Image.fromarray(img, 'RGB')
image = img.resize((shape_of_img, shape_of_img))
img = np.array(image) / 255
a = []
a.append(img)
a = np.array(a)

prediction = model_load.predict(a, verbose=1)
index = np.argmax(prediction)
# print(index)
# print(target_labels)
print("Your image {} is type {}".format('banana.jpeg', target_labels[index]))

img = cv.imread('./data/my_test/kiwi.jpeg')
img = Image.fromarray(img, 'RGB')
image = img.resize((shape_of_img, shape_of_img))
img = np.array(image) / 255
a = []
a.append(img)
a = np.array(a)

prediction = model_load.predict(a, verbose=1)
index = np.argmax(prediction)
# print(index)
# print(target_labels)
print("Your image {} is type {}".format('kiwi.jpeg', target_labels[index]))

img = cv.imread('./data/my_test/apricot.jpeg')
img = Image.fromarray(img, 'RGB')
image = img.resize((shape_of_img, shape_of_img))
img = np.array(image) / 255
a = []
a.append(img)
a = np.array(a)

prediction = model_load.predict(a, verbose=1)
index = np.argmax(prediction)
# print(index)
# print(target_labels)
print("Your image {} is type {}".format('apricot.jpeg', target_labels[index]))
