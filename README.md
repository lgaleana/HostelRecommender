# Item-item recommender system for hostels

It uses Euclidean distance to compare hostel vectors:

d = (x1 - x2)² + ... + (xn - xn)²

where d is the Euclidean distance and xi is an element of the vector. No square root is applied, to emphasize the similarities and differences between hostels. Hostel vectors are represented by attributes, such as "party", "family", etc.

Eeach attribute has a rating, computed by making an NLP analysis over a bunch of reviews of the hostel. The rating is what is effectively used to get the Euclidean distance.

Another type of classification is possible, by providing a list of attributes and meassuring the hostels where those attributes are the most important. The attribute rating is what is effectively used too.

A penalization based on the number of reviews of each hostel is also applied.
